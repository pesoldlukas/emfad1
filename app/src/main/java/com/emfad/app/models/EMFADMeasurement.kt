package com.emfad.app.models

import java.util.Date

data class EMFADMeasurement(
    val id: String,
    val timestamp: Long,
    val frequency: Double,
    val mode: MeasurementMode,
    val orientation: MeasurementOrientation,
    val value: Double,
    val latitude: Double?,
    val longitude: Double?
) {
    enum class MeasurementMode { A, B, A_B, B_A }
    enum class MeasurementOrientation { HORIZONTAL, VERTICAL }

    companion object {
        fun fromBluetoothData(data: ByteArray): EMFADMeasurement? {
            // Implement your actual Bluetooth data parsing here
            return try {
                // Example parsing - adjust to your actual protocol
                val parts = String(data).split(",")
                EMFADMeasurement(
                    id = UUID.randomUUID().toString(),
                    timestamp = System.currentTimeMillis(),
                    frequency = parts[0].toDouble(),
                    mode = MeasurementMode.valueOf(parts[1]),
                    orientation = MeasurementOrientation.valueOf(parts[2]),
                    value = parts[3].toDouble(),
                    latitude = null,
                    longitude = null
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
