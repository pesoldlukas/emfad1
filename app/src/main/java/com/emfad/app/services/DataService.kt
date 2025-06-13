package com.emfad.app.services

import android.content.Context
import android.util.Log
import com.emfad.app.models.EMFADMeasurement
import com.emfad.app.models.MeasurementSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DataService(private val context: Context) {
    private val TAG = "DataService"
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    
    private val _exportProgress = MutableStateFlow<Int>(0)
    val exportProgress: StateFlow<Int> = _exportProgress

    fun exportSessionToFile(session: MeasurementSession): File? {
        return try {
            val fileName = "EMFAD_${dateFormat.format(session.startTime)}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            file.writeText(session.toCSV())
            file
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Exportieren der Sitzung: ${e.message}")
            null
        }
    }

    fun exportMeasurementsToFile(measurements: List<EMFADMeasurement>): File? {
        return try {
            val fileName = "EMFAD_Measurements_${dateFormat.format(Date())}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            val header = "Zeitstempel,E-Feld (V/m),M-Feld (µT),Frequenz (Hz),Batterie (%)"
            val rows = measurements.map { measurement ->
                "${measurement.timestamp},${measurement.electricField},${measurement.magneticField},${measurement.frequency},${measurement.batteryLevel}"
            }
            
            file.writeText((listOf(header) + rows).joinToString("\n"))
            file
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Exportieren der Messungen: ${e.message}")
            null
        }
    }

    fun generateReport(session: MeasurementSession): String {
        val statistics = session.let {
            """
            Messbericht
            -----------
            Startzeit: ${dateFormat.format(it.startTime)}
            Endzeit: ${it.endTime?.let { end -> dateFormat.format(end) } ?: "Laufend"}
            Dauer: ${it.duration / 1000} Sekunden
            Ort: ${it.location}
            Notizen: ${it.notes}
            
            Statistiken:
            - Durchschnittliches E-Feld: ${it.averageElectricField} V/m
            - Durchschnittliches M-Feld: ${it.averageMagneticField} µT
            - Spitzenwert E-Feld: ${it.peakElectricField} V/m
            - Spitzenwert M-Feld: ${it.peakMagneticField} µT
            
            Anzahl Messungen: ${it.measurements.size}
            """.trimIndent()
        }
        
        return statistics
    }

    fun cleanupOldFiles(maxAgeDays: Int = 30) {
        try {
            val directory = context.getExternalFilesDir(null)
            val currentTime = System.currentTimeMillis()
            val maxAgeMillis = maxAgeDays * 24 * 60 * 60 * 1000L
            
            directory?.listFiles()?.forEach { file ->
                if (currentTime - file.lastModified() > maxAgeMillis) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Fehler beim Aufräumen alter Dateien: ${e.message}")
        }
    }
} 