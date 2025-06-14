package com.emfad.app.bluetooth

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EMFADDevice(
    val bluetoothDevice: BluetoothDevice,
    val name: String = bluetoothDevice.name ?: "Unknown device",
    val address: String = bluetoothDevice.address,
    val rssi: Int = 0
) {
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _batteryLevel = MutableStateFlow<Int?>(null)
    val batteryLevel: StateFlow<Int?> = _batteryLevel

    private val _signalStrength = MutableStateFlow<Int?>(null)
    val signalStrength: StateFlow<Int?> = _signalStrength

    private val _measurementMode = MutableStateFlow<MeasurementMode>(MeasurementMode.UNKNOWN)
    val measurementMode: StateFlow<MeasurementMode> = _measurementMode

    fun updateConnectionState(connected: Boolean) {
        _isConnected.value = connected
    }

    fun updateBatteryLevel(level: Int) {
        _batteryLevel.value = level.coerceIn(0, 100)
    }

    fun updateSignalStrength(strength: Int) {
        _signalStrength.value = strength.coerceIn(-100, 0)
    }

    fun updateMeasurementMode(mode: MeasurementMode) {
        _measurementMode.value = mode
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EMFADDevice) return false
        return address == other.address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}

enum class MeasurementMode {
    FERROUS_METAL,
    NON_FERROUS_METAL,
    CAVITY,
    UNKNOWN
}
