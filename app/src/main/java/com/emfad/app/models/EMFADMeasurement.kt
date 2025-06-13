package com.emfad.app.models

import java.util.*

data class EMFADMeasurement(
    val timestamp: Long,
    val electricField: Float, // V/m
    val magneticField: Float, // µT
    val frequency: Float,     // Hz
    val mode: MeasurementMode,
    val batteryLevel: Int = 100 // Prozent
) {
    enum class MeasurementMode { SINGLE, CONTINUOUS, PEAK }

    companion object {
        fun fromRaw(input: String): EMFADMeasurement? {
            // Format: "timestamp,electricField,magneticField,frequency,mode,batteryLevel"
            return try {
                val parts = input.split(",")
                EMFADMeasurement(
                    timestamp = parts[0].toLong(),
                    electricField = parts[1].toFloat(),
                    magneticField = parts[2].toFloat(),
                    frequency = parts[3].toFloat(),
                    mode = MeasurementMode.valueOf(parts[4]),
                    batteryLevel = parts.getOrNull(5)?.toIntOrNull() ?: 100
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toRaw(): String {
        return "$timestamp,$electricField,$magneticField,$frequency,$mode,$batteryLevel"
    }

    fun convertToUnit(unit: ElectricFieldUnit): Float {
        return when (unit) {
            ElectricFieldUnit.V_PER_M -> electricField
            ElectricFieldUnit.KV_PER_M -> electricField / 1000
            ElectricFieldUnit.MV_PER_M -> electricField * 1000
        }
    }

    fun convertToUnit(unit: MagneticFieldUnit): Float {
        return when (unit) {
            MagneticFieldUnit.MICRO_TESLA -> magneticField
            MagneticFieldUnit.MILLI_GAUSS -> magneticField * 10
            MagneticFieldUnit.GAUSS -> magneticField * 0.01f
        }
    }
}

enum class ElectricFieldUnit {
    V_PER_M,
    KV_PER_M,
    MV_PER_M
}

enum class MagneticFieldUnit {
    MICRO_TESLA,
    MILLI_GAUSS,
    GAUSS
} 