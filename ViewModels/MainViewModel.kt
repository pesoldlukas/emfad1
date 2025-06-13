package com.emfad.app.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emfad.app.Models.*
import com.emfad.app.Services.BluetoothService
import com.emfad.app.Services.LocationService
import com.emfad.app.Services.MeasurementService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothService = BluetoothService(application)
    private val locationService = LocationService(application)
    private val measurementService = MeasurementService()

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState

    private val _currentSettings = MutableStateFlow(
        MeasurementSettings(
            frequency = 70.0, // kHz
            mode = MeasurementMode.A,
            orientation = MeasurementOrientation.HORIZONTAL,
            autoInterval = null,
            filterLevel = 0,
            gain = 1.0,
            offset = 0.0
        )
    )
    val currentSettings: StateFlow<MeasurementSettings> = _currentSettings

    init {
        viewModelScope.launch {
            combine(
                bluetoothService.connectionState,
                bluetoothService.measurementData,
                locationService.location,
                measurementService.currentSession,
                measurementService.currentProfile
            ) { connectionState, measurementData, location, session, profile ->
                when (connectionState) {
                    is BluetoothService.ConnectionState.Connected -> {
                        measurementData?.let { data ->
                            // Parse measurement data and update UI
                            val value = parseMeasurementValue(data)
                            measurementService.addMeasurement(
                                value = value,
                                latitude = location?.latitude,
                                longitude = location?.longitude
                            )
                        }
                    }
                    is BluetoothService.ConnectionState.Disconnected -> {
                        _uiState.value = UiState.Disconnected
                    }
                    is BluetoothService.ConnectionState.Error -> {
                        _uiState.value = UiState.Error(connectionState.message)
                    }
                }
            }.collect()
        }
    }

    fun startMeasurement() {
        viewModelScope.launch {
            try {
                locationService.startLocationUpdates()
                measurementService.startNewSession(
                    name = "Measurement_${System.currentTimeMillis()}",
                    settings = _currentSettings.value
                )
                _uiState.value = UiState.Measuring
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun stopMeasurement() {
        viewModelScope.launch {
            locationService.stopLocationUpdates()
            measurementService.endCurrentSession()
            _uiState.value = UiState.Connected
        }
    }

    fun startNewProfile(profileLength: Double, distance: Double) {
        measurementService.startNewProfile(
            name = "Profile_${System.currentTimeMillis()}",
            profileLength = profileLength,
            distance = distance
        )
    }

    fun endCurrentProfile() {
        measurementService.endCurrentProfile()
    }

    fun updateSettings(settings: MeasurementSettings) {
        _currentSettings.value = settings
    }

    private fun parseMeasurementValue(data: ByteArray): Double {
        // Implement parsing logic based on EMFAD protocol
        // This is a placeholder implementation
        return data[0].toDouble()
    }

    sealed class UiState {
        object Initial : UiState()
        object Connected : UiState()
        object Measuring : UiState()
        object Disconnected : UiState()
        data class Error(val message: String) : UiState()
    }
} 