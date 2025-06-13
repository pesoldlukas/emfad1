package com.emfad.app.models

import kotlin.math.*

data class CalibrationData(
    val materialType: MaterialType,
    val depth: Float,
    val magneticField: Float,
    val electricField: Float,
    val frequency: Float,
    val abValue: Float
)

class MaterialCalibration {
    private val calibrationPoints = mutableListOf<CalibrationData>()
    private var calibrationFactors = mutableMapOf<MaterialType, CalibrationFactors>()
    
    data class CalibrationFactors(
        val magneticFactor: Float,
        val electricFactor: Float,
        val depthFactor: Float,
        val frequencyFactor: Float
    )
    
    fun addCalibrationPoint(data: CalibrationData) {
        calibrationPoints.add(data)
        updateCalibrationFactors()
    }
    
    fun removeCalibrationPoint(data: CalibrationData) {
        calibrationPoints.remove(data)
        updateCalibrationFactors()
    }
    
    fun getCalibrationFactors(materialType: MaterialType): CalibrationFactors? {
        return calibrationFactors[materialType]
    }
    
    private fun updateCalibrationFactors() {
        // Gruppiere Kalibrierungspunkte nach Materialtyp
        val groupedPoints = calibrationPoints.groupBy { it.materialType }
        
        // Berechne Kalibrierungsfaktoren für jeden Materialtyp
        groupedPoints.forEach { (materialType, points) ->
            if (points.size >= 2) {
                val factors = calculateFactors(points)
                calibrationFactors[materialType] = factors
            }
        }
    }
    
    private fun calculateFactors(points: List<CalibrationData>): CalibrationFactors {
        // Berechne Durchschnittswerte
        val avgMagnetic = points.map { it.magneticField }.average().toFloat()
        val avgElectric = points.map { it.electricField }.average().toFloat()
        val avgDepth = points.map { it.depth }.average().toFloat()
        val avgFrequency = points.map { it.frequency }.average().toFloat()
        
        // Berechne Standardabweichungen
        val stdMagnetic = calculateStandardDeviation(points.map { it.magneticField })
        val stdElectric = calculateStandardDeviation(points.map { it.electricField })
        val stdDepth = calculateStandardDeviation(points.map { it.depth })
        val stdFrequency = calculateStandardDeviation(points.map { it.frequency })
        
        // Berechne Kalibrierungsfaktoren
        return CalibrationFactors(
            magneticFactor = if (stdMagnetic > 0) 1f / stdMagnetic else 1f,
            electricFactor = if (stdElectric > 0) 1f / stdElectric else 1f,
            depthFactor = if (stdDepth > 0) 1f / stdDepth else 1f,
            frequencyFactor = if (stdFrequency > 0) 1f / stdFrequency else 1f
        )
    }
    
    private fun calculateStandardDeviation(values: List<Float>): Float {
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        return sqrt(variance).toFloat()
    }
    
    fun applyCalibration(
        measurement: EMFADMeasurement,
        materialType: MaterialType
    ): EMFADMeasurement {
        val factors = calibrationFactors[materialType] ?: return measurement
        
        return measurement.copy(
            magneticField = measurement.magneticField * factors.magneticFactor,
            electricField = measurement.electricField * factors.electricFactor,
            frequency = measurement.frequency * factors.frequencyFactor
        )
    }
    
    fun getCalibrationQuality(materialType: MaterialType): Float {
        val points = calibrationPoints.filter { it.materialType == materialType }
        if (points.size < 2) return 0f
        
        // Berechne die Qualität basierend auf der Konsistenz der Messungen
        val magneticConsistency = calculateConsistency(points.map { it.magneticField })
        val electricConsistency = calculateConsistency(points.map { it.electricField })
        val depthConsistency = calculateConsistency(points.map { it.depth })
        
        return (magneticConsistency + electricConsistency + depthConsistency) / 3f
    }
    
    private fun calculateConsistency(values: List<Float>): Float {
        if (values.size < 2) return 0f
        
        val mean = values.average()
        val maxDeviation = values.map { abs(it - mean) }.maxOrNull() ?: 0f
        val range = values.maxOrNull()!! - values.minOrNull()!!
        
        return if (range > 0) 1f - (maxDeviation / range) else 1f
    }
} 