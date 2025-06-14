package com.emfad.app.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.util.*

class BluetoothManager(private val context: Context) {
    private val TAG = "BluetoothManager"
    private val SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var bluetoothSocket: BluetoothSocket? = null
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState
    
    private val _receivedData = MutableStateFlow<String>("")
    val receivedData: StateFlow<String> = _receivedData

    private val _availableDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val availableDevices: StateFlow<List<BluetoothDevice>> = _availableDevices

    sealed class ConnectionState {
        object CONNECTED : ConnectionState()
        object DISCONNECTED : ConnectionState()
        object CONNECTING : ConnectionState()
        object SCANNING : ConnectionState()
        data class ERROR(val message: String) : ConnectionState()
    }

    fun startScan() {
        if (!hasRequiredPermissions()) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
            return
        }

        if (bluetoothAdapter?.isEnabled != true) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth is disabled")
            return
        }

        _connectionState.value = ConnectionState.SCANNING
        val devices = mutableListOf<BluetoothDevice>()

        try {
            bluetoothAdapter.bondedDevices?.forEach { device ->
                if (device.name?.contains("EMFAD", ignoreCase = true) == true) {
                    devices.add(device)
                }
            }
            _availableDevices.value = devices
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission error while scanning: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (!hasRequiredPermissions()) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
            return
        }

        if (bluetoothAdapter?.isEnabled != true) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth is disabled")
            return
        }

        _connectionState.value = ConnectionState.CONNECTING
        
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket?.connect()
            _connectionState.value = ConnectionState.CONNECTED
            startListening()
        } catch (e: IOException) {
            Log.e(TAG, "Connection error: ${e.message}")
            _connectionState.value = ConnectionState.ERROR(e.message ?: "Unknown error")
            closeConnection()
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission error: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
            closeConnection()
        }
    }

    fun sendData(data: String) {
        if (!hasRequiredPermissions() || _connectionState.value != ConnectionState.CONNECTED) {
            _connectionState.value = ConnectionState.ERROR("Not connected or permissions missing")
            return
        }

        try {
            bluetoothSocket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            Log.e(TAG, "Send error: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Error sending data")
            closeConnection()
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission error while sending: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
            closeConnection()
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startListening() {
        Thread {
            val buffer = ByteArray(1024)
            while (_connectionState.value == ConnectionState.CONNECTED) {
                try {
                    if (!hasRequiredPermissions()) {
                        _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
                        break
                    }
                    
                    val bytes = bluetoothSocket?.inputStream?.read(buffer) ?: 0
                    if (bytes > 0) {
                        val data = String(buffer, 0, bytes)
                        _receivedData.value = data
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Read error: ${e.message}")
                    _connectionState.value = ConnectionState.ERROR(e.message ?: "Read error")
                    break
                } catch (e: SecurityException) {
                    Log.e(TAG, "Permission error: ${e.message}")
                    _connectionState.value = ConnectionState.ERROR("Bluetooth permissions missing")
                    break
                }
            }
        }.start()
    }

    fun closeConnection() {
        try {
            if (hasRequiredPermissions()) {
                bluetoothSocket?.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error closing connection: ${e.message}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission error while closing: ${e.message}")
        } finally {
            bluetoothSocket = null
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }
}
