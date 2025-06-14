package com.emfad.app.models

data class CalibrationData(
    val materialType: MaterialType,
    val depth: Float,
    val value: Float,
    val frequency: Float
)

class MaterialCalibration {
    private val calibrationPoints = mutableListOf<CalibrationData>()
    
    data class CalibrationFactors(
        val factor: Float,
        val offset: Float
    )
    
    fun addCalibrationPoint(data: CalibrationData) {
        calibrationPoints.add(data)
    }
    
    fun removeCalibrationPoint(data: CalibrationData) {
        calibrationPoints.remove(data)
    }
    
    fun getCalibrationFactors(materialType: MaterialType): CalibrationFactors? {
        val points = calibrationPoints.filter { it.materialType == materialType }
        if (points.size < 2) return null
        
        // Simple linear calibration: value = factor * raw + offset
        val sumX = points.sumOf { it.value.toDouble() }
        val sumY = points.sumOf { it.depth.toDouble() }
        val sumXY = points.sumOf { it.value * it.depth }
        val sumX2 = points.sumOf { it.value * it.value }
        
        val n = points.size
        val factor = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX)
        val offset = (sumY - factor * sumX) / n
        
        return CalibrationFactors(factor.toFloat(), offset.toFloat())
    }
    
    fun applyCalibration(
        rawValue: Float,
        materialType: MaterialType
    ): Float {
        val factors = getCalibrationFactors(materialType) ?: return rawValue
        return factors.factor * rawValue + factors.offset
    }
    
    fun getCalibrationQuality(materialType: MaterialType): Float {
        val points = calibrationPoints.filter { it.materialType == materialType }
        if (points.size < 2) return 0f
        
        // Calculate R-squared value
        val factors = getCalibrationFactors(materialType) ?: return 0f
        val meanY = points.map { it.depth }.average()
        
        var ssTotal = 0.0
        var ssResidual = 0.0
        
        for (point in points) {
            val predicted = factors.factor * point.value + factors.offset
            ssTotal += (point.depth - meanY) * (point.depth - meanY)
            ssResidual += (point.depth - predicted) * (point.depth - predicted)
        }
        
        return (1 - (ssResidual / ssTotal)).toFloat().coerceIn(0f, 1f)
    }
}
