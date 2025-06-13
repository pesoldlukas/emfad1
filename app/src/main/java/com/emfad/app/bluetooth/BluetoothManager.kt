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
            _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
            return
        }

        if (bluetoothAdapter?.isEnabled != true) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth ist deaktiviert")
            return
        }

        _connectionState.value = ConnectionState.SCANNING
        val devices = mutableListOf<BluetoothDevice>()

        try {
            bluetoothAdapter?.bondedDevices?.forEach { device ->
                if (device.name?.contains("EMFAD", ignoreCase = true) == true) {
                    devices.add(device)
                }
            }
            _availableDevices.value = devices
        } catch (e: SecurityException) {
            Log.e(TAG, "Berechtigungsfehler beim Scannen: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
        }
    }

    fun connectToDevice(device: BluetoothDevice) {
        if (!hasRequiredPermissions()) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
            return
        }

        if (bluetoothAdapter?.isEnabled != true) {
            _connectionState.value = ConnectionState.ERROR("Bluetooth ist deaktiviert")
            return
        }

        _connectionState.value = ConnectionState.CONNECTING
        
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(SPP_UUID)
            bluetoothSocket?.connect()
            _connectionState.value = ConnectionState.CONNECTED
            startListening()
        } catch (e: IOException) {
            Log.e(TAG, "Verbindungsfehler: ${e.message}")
            _connectionState.value = ConnectionState.ERROR(e.message ?: "Unbekannter Fehler")
            closeConnection()
        } catch (e: SecurityException) {
            Log.e(TAG, "Berechtigungsfehler: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
            closeConnection()
        }
    }

    fun sendData(data: String) {
        if (!hasRequiredPermissions() || _connectionState.value != ConnectionState.CONNECTED) {
            _connectionState.value = ConnectionState.ERROR("Nicht verbunden oder Berechtigungen fehlen")
            return
        }

        try {
            bluetoothSocket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            Log.e(TAG, "Fehler beim Senden: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Fehler beim Senden der Daten")
            closeConnection()
        } catch (e: SecurityException) {
            Log.e(TAG, "Berechtigungsfehler beim Senden: ${e.message}")
            _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
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
                        _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
                        break
                    }
                    
                    val bytes = bluetoothSocket?.inputStream?.read(buffer)
                    if (bytes != null && bytes > 0) {
                        val data = String(buffer, 0, bytes)
                        _receivedData.value = data
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Lesefehler: ${e.message}")
                    _connectionState.value = ConnectionState.ERROR(e.message ?: "Lesefehler")
                    break
                } catch (e: SecurityException) {
                    Log.e(TAG, "Berechtigungsfehler: ${e.message}")
                    _connectionState.value = ConnectionState.ERROR("Bluetooth-Berechtigungen fehlen")
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
            Log.e(TAG, "Fehler beim Schließen der Verbindung: ${e.message}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Berechtigungsfehler beim Schließen: ${e.message}")
        } finally {
            bluetoothSocket = null
            _connectionState.value = ConnectionState.DISCONNECTED
        }
    }
} 