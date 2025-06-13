package com.emfad.app.models

import java.util.*

data class MeasurementSession(
    val id: String = UUID.randomUUID().toString(),
    val deviceId: String,
    val location: String,
    val notes: String?,
    val type: SessionType,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val measurements: MutableList<EMFADMeasurement> = mutableListOf(),
    val tags: Set<String> = emptySet()
) {
    enum class SessionType { STANDARD, COMPLIANCE, RESEARCH }

    val duration: Long
        get() = (endTime ?: System.currentTimeMillis()) - startTime

    val averageElectricField: Float
        get() = measurements.map { it.electricField }.average().toFloat()

    val peakElectricField: Float
        get() = measurements.maxOfOrNull { it.electricField } ?: 0f

    val averageMagneticField: Float
        get() = measurements.map { it.magneticField }.average().toFloat()

    val peakMagneticField: Float
        get() = measurements.maxOfOrNull { it.magneticField } ?: 0f

    fun addMeasurement(measurement: EMFADMeasurement) {
        measurements.add(measurement)
    }

    fun end() {
        endTime = System.currentTimeMillis()
    }

    fun exportToCsv(): String {
        val header = "timestamp,electricField,magneticField,frequency,mode,batteryLevel"
        val body = measurements.joinToString("\n") { it.toRaw() }
        return "$header\n$body"
    }

    fun getStatistics(): Map<String, Float> {
        return mapOf(
            "Durchschnittliches E-Feld" to averageElectricField,
            "Spitzenwert E-Feld" to peakElectricField,
            "Durchschnittliches M-Feld" to averageMagneticField,
            "Spitzenwert M-Feld" to peakMagneticField,
            "Messdauer" to (duration / 1000f),
            "Anzahl Messungen" to measurements.size.toFloat()
        )
    }

    fun addTag(tag: String) {
        (tags as MutableSet).add(tag)
    }

    fun removeTag(tag: String) {
        (tags as MutableSet).remove(tag)
    }

    fun hasTag(tag: String): Boolean {
        return tags.contains(tag)
    }
} 