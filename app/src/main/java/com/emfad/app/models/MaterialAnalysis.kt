package com.emfad.app.models

data class MaterialAnalysis(
    val type: MaterialType,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MaterialType {
    METAL,
    CAVITY,
    UNKNOWN
}

object MaterialAnalyzer {
    private const val METAL_THRESHOLD_MAGNETIC = 50.0 // µT
    private const val CAVITY_THRESHOLD_ELECTRIC = 30.0 // V/m
    private const val CONFIDENCE_THRESHOLD = 0.7f

    fun analyzeMeasurement(measurement: EMFADMeasurement): MaterialAnalysis {
        // For simplicity, using magnetic field for metal detection
        val magneticValue = measurement.value
        
        // Metal detection based on magnetic field
        val metalConfidence = calculateMetalConfidence(magneticValue)
        
        return if (metalConfidence > CONFIDENCE_THRESHOLD) {
            MaterialAnalysis(MaterialType.METAL, metalConfidence)
        } else {
            MaterialAnalysis(MaterialType.UNKNOWN, 0f)
        }
    }

    private fun calculateMetalConfidence(magneticValue: Double): Float {
        return (magneticValue / METAL_THRESHOLD_MAGNETIC).toFloat().coerceIn(0f, 1f)
    }
}
