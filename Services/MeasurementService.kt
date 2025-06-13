package com.emfad.app.Services

import com.emfad.app.Models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class MeasurementService {
    private val _currentSession = MutableStateFlow<MeasurementSession?>(null)
    val currentSession: StateFlow<MeasurementSession?> = _currentSession

    private val _currentProfile = MutableStateFlow<MeasurementProfile?>(null)
    val currentProfile: StateFlow<MeasurementProfile?> = _currentProfile

    fun startNewSession(
        name: String,
        settings: MeasurementSettings
    ): MeasurementSession {
        val session = MeasurementSession(
            id = UUID.randomUUID().toString(),
            name = name,
            profiles = emptyList(),
            startTime = System.currentTimeMillis(),
            endTime = null,
            settings = settings
        )
        _currentSession.value = session
        return session
    }

    fun endCurrentSession() {
        _currentSession.value?.let { session ->
            _currentSession.value = session.copy(
                endTime = System.currentTimeMillis()
            )
        }
    }

    fun startNewProfile(
        name: String,
        profileLength: Double,
        distance: Double
    ): MeasurementProfile {
        val profile = MeasurementProfile(
            id = UUID.randomUUID().toString(),
            name = name,
            measurements = emptyList(),
            profileLength = profileLength,
            distance = distance
        )
        _currentProfile.value = profile
        return profile
    }

    fun addMeasurement(
        value: Double,
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        _currentProfile.value?.let { profile ->
            val measurement = MeasurementData(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis(),
                frequency = _currentSession.value?.settings?.frequency ?: 0.0,
                mode = _currentSession.value?.settings?.mode ?: MeasurementMode.A,
                orientation = _currentSession.value?.settings?.orientation ?: MeasurementOrientation.HORIZONTAL,
                value = value,
                latitude = latitude,
                longitude = longitude
            )

            val updatedProfile = profile.copy(
                measurements = profile.measurements + measurement
            )
            _currentProfile.value = updatedProfile

            _currentSession.value?.let { session ->
                val updatedProfiles = session.profiles.toMutableList()
                val profileIndex = updatedProfiles.indexOfFirst { it.id == profile.id }
                if (profileIndex >= 0) {
                    updatedProfiles[profileIndex] = updatedProfile
                } else {
                    updatedProfiles.add(updatedProfile)
                }
                _currentSession.value = session.copy(profiles = updatedProfiles)
            }
        }
    }

    fun endCurrentProfile() {
        _currentProfile.value = null
    }

    fun applyFilter(measurements: List<MeasurementData>, filterLevel: Int): List<MeasurementData> {
        if (filterLevel <= 0) return measurements

        return measurements.windowed(filterLevel, 1, true) { window ->
            val filteredValue = window.map { it.value }.average()
            window.first().copy(value = filteredValue)
        }
    }

    fun calculateStatistics(measurements: List<MeasurementData>): MeasurementStatistics {
        val values = measurements.map { it.value }
        return MeasurementStatistics(
            min = values.minOrNull() ?: 0.0,
            max = values.maxOrNull() ?: 0.0,
            average = values.average(),
            standardDeviation = calculateStandardDeviation(values)
        )
    }

    private fun calculateStandardDeviation(values: List<Double>): Double {
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return kotlin.math.sqrt(variance)
    }
}

data class MeasurementStatistics(
    val min: Double,
    val max: Double,
    val average: Double,
    val standardDeviation: Double
) 