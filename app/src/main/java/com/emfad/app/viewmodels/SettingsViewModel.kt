package com.emfad.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val frequency: Double = 1000.0,
    val antennaDistance: Double = 1.0,
    val measurementAccuracy: Float = 0.8f,
    val autoCalibration: Boolean = true,
    val calibrationAccuracy: Float = 0.8f,
    val metalDetection: Boolean = true,
    val crystalDetection: Boolean = true,
    val clusterAnalysis: Boolean = true,
    val inclusionAnalysis: Boolean = true
)

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    fun setFrequency(frequency: Double) {
        _uiState.value = _uiState.value.copy(frequency = frequency)
    }
    
    fun setAntennaDistance(distance: Double) {
        _uiState.value = _uiState.value.copy(antennaDistance = distance)
    }
    
    fun setMeasurementAccuracy(accuracy: Float) {
        _uiState.value = _uiState.value.copy(measurementAccuracy = accuracy)
    }
    
    fun setAutoCalibration(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoCalibration = enabled)
    }
    
    fun setCalibrationAccuracy(accuracy: Float) {
        _uiState.value = _uiState.value.copy(calibrationAccuracy = accuracy)
    }
    
    fun setMetalDetection(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(metalDetection = enabled)
    }
    
    fun setCrystalDetection(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(crystalDetection = enabled)
    }
    
    fun setClusterAnalysis(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(clusterAnalysis = enabled)
    }
    
    fun setInclusionAnalysis(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(inclusionAnalysis = enabled)
    }
    
    fun saveSettings() {
        viewModelScope.launch {
            // TODO: Einstellungen in SharedPreferences speichern
        }
    }
    
    fun loadSettings() {
        viewModelScope.launch {
            // TODO: Einstellungen aus SharedPreferences laden
        }
    }
} 