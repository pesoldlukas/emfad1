package com.emfad.app.services

import android.content.Context
import android.util.Log
import com.emfad.app.data.MeasurementDatabase
import com.emfad.app.models.EMFADMeasurement
import com.emfad.app.models.MeasurementSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MeasurementService(private val context: Context) {
    private val TAG = "MeasurementService"
    private val database = MeasurementDatabase.getDatabase(context)
    
    private val _currentSession = MutableStateFlow<MeasurementSession?>(null)
    val currentSession: StateFlow<MeasurementSession?> = _currentSession
    
    private val _measurements = MutableStateFlow<List<EMFADMeasurement>>(emptyList())
    val measurements: StateFlow<List<EMFADMeasurement>> = _measurements

    suspend fun startNewSession(
        deviceId: String,
        location: String = "",
        notes: String = "",
        sessionType: MeasurementSession.SessionType = MeasurementSession.SessionType.STANDARD
    ) {
        val session = MeasurementSession(
            deviceId = deviceId,
            location = location,
            notes = notes,
            sessionType = sessionType
        )
        _currentSession.value = session
        _measurements.value = emptyList()
    }

    suspend fun addMeasurement(rawData: String) {
        val measurement = EMFADMeasurement.fromRawData(rawData)
        measurement?.let {
            _currentSession.value?.addMeasurement(it)
            _measurements.value = _measurements.value + it
            saveMeasurement(it)
        }
    }

    private suspend fun saveMeasurement(measurement: EMFADMeasurement) {
        try {
            database.measurementDao().insertMeasurement(measurement)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Speichern der Messung: ${e.message}")
        }
    }

    suspend fun endCurrentSession() {
        _currentSession.value?.endSession()
        _currentSession.value = null
    }

    suspend fun getMeasurementsInRange(startTime: Date, endTime: Date): List<EMFADMeasurement> {
        return try {
            database.measurementDao().getMeasurementsInRange(startTime, endTime)
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Abrufen der Messungen: ${e.message}")
            emptyList()
        }
    }

    suspend fun exportSessionToCSV(session: MeasurementSession): String {
        return session.toCSV()
    }

    fun getSessionStatistics(session: MeasurementSession): Map<String, Double> {
        return mapOf(
            "Durchschnittliches E-Feld" to session.averageElectricField,
            "Durchschnittliches M-Feld" to session.averageMagneticField,
            "Spitzenwert E-Feld" to session.peakElectricField,
            "Spitzenwert M-Feld" to session.peakMagneticField
        )
    }
} 