package com.emfad.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emfad.app.models.EMFADDevice
import com.emfad.app.services.BluetoothService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BluetoothViewModel(application: Application) : AndroidViewModel(application) {
    private val bluetoothService = BluetoothService(application)
    
    private val _uiState = MutableStateFlow<BluetoothUiState>(BluetoothUiState.Initial)
    val uiState: StateFlow<BluetoothUiState> = _uiState.asStateFlow()
    
    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        viewModelScope.launch {
            bluetoothService.connectionState.collect { state ->
                _uiState.value = when (state) {
                    is BluetoothService.ConnectionState.CONNECTED -> BluetoothUiState.Connected
                    is BluetoothService.ConnectionState.CONNECTING -> BluetoothUiState.Connecting
                    is BluetoothService.ConnectionState.DISCONNECTED -> BluetoothUiState.Disconnected
                    is BluetoothService.ConnectionState.ERROR -> BluetoothUiState.Error(state.message)
                }
            }
        }
    }

    fun startDeviceScan() {
        _isScanning.value = true
        bluetoothService.startDiscovery()
    }

    fun stopDeviceScan() {
        _isScanning.value = false
        bluetoothService.stopDiscovery()
    }

    fun connectToDevice(device: EMFADDevice) {
        bluetoothService.connectToDevice(device)
    }

    fun disconnect() {
        bluetoothService.disconnect()
    }

    fun onDeviceFound(device: EMFADDevice) {
        bluetoothService.onDeviceFound(device.bluetoothDevice)
    }

    sealed class BluetoothUiState {
        object Initial : BluetoothUiState()
        object Connected : BluetoothUiState()
        object Connecting : BluetoothUiState()
        object Disconnected : BluetoothUiState()
        data class Error(val message: String) : BluetoothUiState()
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothService.disconnect()
    }
} 