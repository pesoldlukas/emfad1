package com.emfad.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emfad.app.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val currentMode: MeasurementMode = MeasurementMode.BA_VERTICAL,
    val isMeasuring: Boolean = false,
    val isCalibrating: Boolean = false,
    val lastMeasurement: MeasurementResult? = null,
    val lastCalibration: CalibrationResult? = null,
    val materialAnalysis: MaterialAnalysisResult? = null,
    val error: String? = null
)

data class MaterialAnalysisResult(
    val materialType: MaterialType,
    val materialName: String,
    val depth: Double,
    val size: Double,
    val confidence: Double,
    val properties: MaterialProperties
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val measurementController = MeasurementController()
    private val automaticCalibration = AutomaticCalibration()
    private val materialDatabase = MaterialDatabase()
    private val metalAnalyzer = MetalAnalyzer()
    private val crystalDetector = CrystalDetector()
    private val clusterAnalyzer = ClusterAnalyzer()
    private val inclusionDetector = InclusionDetector()
    
    fun setMeasurementMode(mode: MeasurementMode) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentMode = mode,
                error = null
            )
            measurementController.setMode(mode)
            automaticCalibration.setMode(mode)
        }
    }
    
    fun startMeasurement() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isMeasuring = true,
                    error = null
                )
                
                val result = measurementController.measure()
                _uiState.value = _uiState.value.copy(
                    lastMeasurement = result,
                    isMeasuring = false
                )
                
                // Führe Materialanalyse durch
                analyzeMaterial(result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isMeasuring = false,
                    error = "Messfehler: ${e.message}"
                )
            }
        }
    }
    
    fun startCalibration() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isCalibrating = true,
                    error = null
                )
                
                val result = automaticCalibration.calibrate()
                _uiState.value = _uiState.value.copy(
                    lastCalibration = result,
                    isCalibrating = false
                )
                
                if (result.success) {
                    measurementController.updateConfig(
                        MeasurementConfig(
                            mode = result.mode,
                            frequency = 1000.0, // Standardfrequenz
                            antennaDistance = 1.0, // Standardabstand
                            calibrationFactor = result.calibrationFactor,
                            isCalibrated = true
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCalibrating = false,
                    error = "Kalibrierungsfehler: ${e.message}"
                )
            }
        }
    }
    
    fun addCalibrationPoint(point: CalibrationPoint) {
        viewModelScope.launch {
            try {
                automaticCalibration.addCalibrationPoint(point)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Fehler beim Hinzufügen des Kalibrierungspunkts: ${e.message}"
                )
            }
        }
    }
    
    private fun analyzeMaterial(measurement: MeasurementResult) {
        viewModelScope.launch {
            try {
                // 1. Metallanalyse
                val metalResult = metalAnalyzer.analyzeMetal(
                    impedanceCurve = listOf(
                        measurement.frequency to measurement.impedance
                    ),
                    frequency = measurement.frequency
                )
                
                // 2. Kristallanalyse
                val crystalResult = crystalDetector.detectCrystal(
                    measuredZ = measurement.impedance,
                    backgroundZ = Complex(377.0, 0.0),
                    frequency = measurement.frequency,
                    noiseStdDev = 0.1
                )
                
                // 3. Clusteranalyse
                val clusterResult = clusterAnalyzer.analyzeClusters(
                    listOf(
                        MeasurementPoint(
                            impedance = measurement.impedance,
                            conductivity = metalResult.conductivity,
                            permittivity = crystalResult.permittivity,
                            permeability = 1.0,
                            depth = measurement.depth,
                            position = Point3D(0.0, 0.0, measurement.depth)
                        )
                    )
                )
                
                // 4. Einschlussanalyse
                val inclusionResult = inclusionDetector.detectInclusion(
                    measuredZ = measurement.impedance,
                    frequency = measurement.frequency,
                    depth = measurement.depth,
                    surroundingMaterial = MaterialProperties(
                        name = "Erde",
                        conductivity = 1e-3,
                        permittivity = Complex(4.0, 0.0),
                        permeability = 1.0,
                        density = 1.5,
                        type = MaterialType.UNKNOWN,
                        color = "Braun",
                        typicalDepth = 0.0,
                        typicalSize = 0.0
                    )
                )
                
                // Bestimme das wahrscheinlichste Material
                val material = materialDatabase.findMatchingMaterial(
                    conductivity = metalResult.conductivity,
                    permittivity = crystalResult.permittivity,
                    permeability = 1.0,
                    density = 2.0 // Standarddichte
                )
                
                if (material != null) {
                    _uiState.value = _uiState.value.copy(
                        materialAnalysis = MaterialAnalysisResult(
                            materialType = material.type,
                            materialName = material.name,
                            depth = measurement.depth,
                            size = material.typicalSize,
                            confidence = measurement.confidence,
                            properties = material
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Analysefehler: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 