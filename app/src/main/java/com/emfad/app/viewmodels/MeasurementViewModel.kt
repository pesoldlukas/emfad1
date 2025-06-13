package com.emfad.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emfad.app.models.EMFADMeasurement
import com.emfad.app.models.MeasurementSession
import com.emfad.app.services.DataService
import com.emfad.app.services.MeasurementService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MeasurementViewModel(application: Application) : AndroidViewModel(application) {
    private val measurementService = MeasurementService(application)
    private val dataService = DataService(application)
    
    private val _uiState = MutableStateFlow<MeasurementUiState>(MeasurementUiState.Initial)
    val uiState: StateFlow<MeasurementUiState> = _uiState.asStateFlow()
    
    private val _currentSession = MutableStateFlow<MeasurementSession?>(null)
    val currentSession: StateFlow<MeasurementSession?> = _currentSession.asStateFlow()
    
    private val _measurements = MutableStateFlow<List<EMFADMeasurement>>(emptyList())
    val measurements: StateFlow<List<EMFADMeasurement>> = _measurements.asStateFlow()
    
    private val _statistics = MutableStateFlow<Map<String, Double>>(emptyMap())
    val statistics: StateFlow<Map<String, Double>> = _statistics.asStateFlow()

    private val _materialAnalysis = MutableStateFlow<MaterialAnalysis?>(null)
    val materialAnalysis: StateFlow<MaterialAnalysis?> = _materialAnalysis.asStateFlow()

    init {
        viewModelScope.launch {
            measurementService.currentSession.collect { session ->
                _currentSession.value = session
                session?.let { updateStatistics(it) }
            }
        }
        
        viewModelScope.launch {
            measurementService.measurements.collect { measurements ->
                _measurements.value = measurements
                measurements.lastOrNull()?.let { measurement ->
                    _materialAnalysis.value = MaterialAnalyzer.analyzeMeasurement(measurement)
                }
            }
        }
    }

    fun startNewSession(
        deviceId: String,
        location: String = "",
        notes: String = "",
        sessionType: MeasurementSession.SessionType = MeasurementSession.SessionType.STANDARD
    ) {
        viewModelScope.launch {
            measurementService.startNewSession(deviceId, location, notes, sessionType)
            _uiState.value = MeasurementUiState.Measuring
        }
    }

    fun addMeasurement(rawData: String) {
        viewModelScope.launch {
            measurementService.addMeasurement(rawData)
        }
    }

    fun endCurrentSession() {
        viewModelScope.launch {
            measurementService.endCurrentSession()
            _uiState.value = MeasurementUiState.SessionEnded
        }
    }

    fun exportCurrentSession() {
        viewModelScope.launch {
            _currentSession.value?.let { session ->
                val file = dataService.exportSessionToFile(session)
                _uiState.value = if (file != null) {
                    MeasurementUiState.ExportSuccess(file.absolutePath)
                } else {
                    MeasurementUiState.ExportError
                }
            }
        }
    }

    private fun updateStatistics(session: MeasurementSession) {
        _statistics.value = measurementService.getSessionStatistics(session)
    }

    fun generateReport(): String {
        return _currentSession.value?.let { session ->
            dataService.generateReport(session)
        } ?: "Keine aktive Messsitzung"
    }

    sealed class MeasurementUiState {
        object Initial : MeasurementUiState()
        object Measuring : MeasurementUiState()
        object SessionEnded : MeasurementUiState()
        data class ExportSuccess(val filePath: String) : MeasurementUiState()
        object ExportError : MeasurementUiState()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            measurementService.endCurrentSession()
        }
    }
} 