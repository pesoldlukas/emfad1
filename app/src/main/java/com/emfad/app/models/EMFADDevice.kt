package com.emfad.app.models

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

data class EMFADDevice(
    val bluetoothDevice: BluetoothDevice,
    val name: String = if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    bluetoothDevice.name ?: "EMFAD UG12DS",
    val address: String = bluetoothDevice.address,
    val isConnected: Boolean = false,
    val firmwareVersion: String = "",
    val serialNumber: String = "",
    val lastCalibrationDate: String = "",
    val measurementRanges: MeasurementRanges = MeasurementRanges()
) {
    data class MeasurementRanges(
        val electricFieldMin: Double = 0.0,    // V/m
        val electricFieldMax: Double = 2000.0,  // V/m
        val magneticFieldMin: Double = 0.0,     // µT
        val magneticFieldMax: Double = 2000.0,  // µT
        val frequencyMin: Double = 5.0,         // Hz
        val frequencyMax: Double = 100000.0     // Hz
    )

    companion object {
        fun isEMFADDevice(device: BluetoothDevice): Boolean {
            // Überprüft, ob es sich um ein EMFAD-Gerät handelt
            // Dies kann anhand des Gerätenamens oder der MAC-Adresse erfolgen
            return if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            device.name?.contains("EMFAD", ignoreCase = true) == true ||
                   device.address.startsWith("00:11:22") // Beispiel-MAC-Präfix
        }
    }
} 