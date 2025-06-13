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
        val magneticField = measurement.magneticField
        val electricField = measurement.electricField
        
        // Metall-Erkennung basierend auf magnetischem Feld
        val metalConfidence = calculateMetalConfidence(magneticField)
        
        // Hohlraum-Erkennung basierend auf elektrischem Feld
        val cavityConfidence = calculateCavityConfidence(electricField)
        
        return when {
            metalConfidence > CONFIDENCE_THRESHOLD -> 
                MaterialAnalysis(MaterialType.METAL, metalConfidence)
            cavityConfidence > CONFIDENCE_THRESHOLD -> 
                MaterialAnalysis(MaterialType.CAVITY, cavityConfidence)
            else -> 
                MaterialAnalysis(MaterialType.UNKNOWN, 0f)
        }
    }

    private fun calculateMetalConfidence(magneticField: Double): Float {
        return (magneticField / METAL_THRESHOLD_MAGNETIC).toFloat().coerceIn(0f, 1f)
    }

    private fun calculateCavityConfidence(electricField: Double): Float {
        return (electricField / CAVITY_THRESHOLD_ELECTRIC).toFloat().coerceIn(0f, 1f)
    }
} 