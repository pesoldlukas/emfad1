package com.emfad.app.models

import kotlin.math.abs
import kotlin.math.sqrt

data class CalibrationResult(
    val success: Boolean,
    val calibrationFactor: Double,
    val confidence: Double,
    val mode: MeasurementMode,
    val timestamp: Long = System.currentTimeMillis()
)

data class CalibrationPoint(
    val position: Point3D,
    val impedance: Complex,
    val frequency: Double,
    val mode: MeasurementMode
)

class AutomaticCalibration {
    companion object {
        private const val MIN_CALIBRATION_POINTS = 3
        private const val MAX_CALIBRATION_POINTS = 10
        private const val MIN_CONFIDENCE = 0.7
    }
    
    private val calibrationPoints = mutableListOf<CalibrationPoint>()
    private var currentMode: MeasurementMode = MeasurementMode.BA_VERTICAL
    
    fun setMode(mode: MeasurementMode) {
        currentMode = mode
        calibrationPoints.clear()
    }
    
    fun addCalibrationPoint(point: CalibrationPoint) {
        if (point.mode != currentMode) {
            throw IllegalArgumentException("Calibration point must match current mode")
        }
        
        if (calibrationPoints.size >= MAX_CALIBRATION_POINTS) {
            calibrationPoints.removeAt(0)
        }
        
        calibrationPoints.add(point)
    }
    
    fun removeCalibrationPoint(index: Int) {
        if (index in calibrationPoints.indices) {
            calibrationPoints.removeAt(index)
        }
    }
    
    fun calibrate(): CalibrationResult {
        if (calibrationPoints.size < MIN_CALIBRATION_POINTS) {
            return CalibrationResult(
                success = false,
                calibrationFactor = 1.0,
                confidence = 0.0,
                mode = currentMode
            )
        }
        
        // 1. Calculate calibration factor
        val calibrationFactor = calculateCalibrationFactor()
        
        // 2. Calculate confidence
        val confidence = calculateCalibrationConfidence(calibrationFactor)
        
        return CalibrationResult(
            success = confidence >= MIN_CONFIDENCE,
            calibrationFactor = calibrationFactor,
            confidence = confidence,
            mode = currentMode
        )
    }
    
    private fun calculateCalibrationFactor(): Double {
        // Calculate average calibration factor
        val factors = calibrationPoints.map { point ->
            calculatePointCalibrationFactor(point)
        }
        
        // Remove outliers
        val filteredFactors = removeOutliers(factors)
        
        return filteredFactors.average()
    }
    
    private fun calculatePointCalibrationFactor(point: CalibrationPoint): Double {
        // Calculate calibration factor for a single point
        val expectedImpedance = when (point.mode) {
            MeasurementMode.BA_VERTICAL -> Complex(377.0, 0.0)
            MeasurementMode.AB_HORIZONTAL -> Complex(377.0, 0.0)
            MeasurementMode.ANTENNA_A -> Complex(377.0, 0.0)
            MeasurementMode.DEPTH_PRO -> Complex(377.0, 0.0)
        }
        
        return expectedImpedance.magnitude / point.impedance.magnitude
    }
    
    private fun removeOutliers(values: List<Double>): List<Double> {
        if (values.size <= 2) return values
        
        val mean = values.average()
        val stdDev = sqrt(values.map { (it - mean).pow(2) }.average())
        
        return values.filter { abs(it - mean) <= 2 * stdDev }
    }
    
    private fun calculateCalibrationConfidence(calibrationFactor: Double): Double {
        var confidence = 0.0
        
        // 1. Number of calibration points (30%)
        val pointConfidence = (calibrationPoints.size.toDouble() / MAX_CALIBRATION_POINTS)
            .coerceIn(0.0, 1.0)
        confidence += 0.3 * pointConfidence
        
        // 2. Spread of calibration factors (30%)
        val factors = calibrationPoints.map { calculatePointCalibrationFactor(it) }
        val mean = factors.average()
        val stdDev = sqrt(factors.map { (it - mean).pow(2) }.average())
        val spreadConfidence = (1.0 - stdDev / mean).coerceIn(0.0, 1.0)
        confidence += 0.3 * spreadConfidence
        
        // 3. Deviation from expected factor (20%)
        val factorDeviation = abs(calibrationFactor - 1.0)
        val factorConfidence = (1.0 - factorDeviation).coerceIn(0.0, 1.0)
        confidence += 0.2 * factorConfidence
        
        // 4. Frequency dependency (20%)
        val frequencies = calibrationPoints.map { it.frequency }
        val freqStdDev = sqrt(frequencies.map { (it - frequencies.average()).pow(2) }.average())
        val freqConfidence = (1.0 - freqStdDev / frequencies.average()).coerceIn(0.0, 1.0)
        confidence += 0.2 * freqConfidence
        
        return confidence.coerceIn(0.0, 1.0)
    }
    
    fun getCalibrationPoints(): List<CalibrationPoint> = calibrationPoints.toList()
    
    fun clearCalibrationPoints() {
        calibrationPoints.clear()
    }
}

data class Point3D(val x: Double, val y: Double, val z: Double)

data class Complex(val real: Double, val imag: Double) {
    val magnitude: Double get() = kotlin.math.sqrt(real * real + imag * imag)
}
