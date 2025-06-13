package com.emfad.app.models

data class MaterialSignature(
    val magneticField: Float,
    val electricField: Float,
    val type: MaterialType
)

enum class MaterialType {
    FERROUS_METAL,    // Eisenhaltige Metalle
    NON_FERROUS_METAL, // Nicht-eisenhaltige Metalle
    CAVITY,           // Hohlraum
    UNKNOWN
}

class MaterialClassifier(
    private var ferrousMagneticThreshold: Float = 80f,  // µT
    private var nonFerrousElectricThreshold: Float = 30f,  // V/m
    private var nonFerrousMagneticThreshold: Float = 20f  // µT
) {
    companion object {
        // Referenzsignaturen für verschiedene Materialien
        private val REFERENCE_SIGNATURES = mapOf(
            "Eisen" to MaterialSignature(120f, 10f, MaterialType.FERROUS_METAL),
            "Kupfer" to MaterialSignature(10f, 35f, MaterialType.NON_FERROUS_METAL),
            "Silber" to MaterialSignature(5f, 40f, MaterialType.NON_FERROUS_METAL),
            "Hohlraum" to MaterialSignature(2f, 15f, MaterialType.CAVITY)
        )
    }

    fun updateThresholds(
        ferrousMagnetic: Float? = null,
        nonFerrousElectric: Float? = null,
        nonFerrousMagnetic: Float? = null
    ) {
        ferrousMagnetic?.let { ferrousMagneticThreshold = it }
        nonFerrousElectric?.let { nonFerrousElectricThreshold = it }
        nonFerrousMagnetic?.let { nonFerrousMagneticThreshold = it }
    }

    fun classifyMaterial(measurement: EMFADMeasurement): MaterialAnalysis {
        val magneticField = measurement.magneticField
        val electricField = measurement.electricField
        
        // Basisklassifizierung
        val baseType = when {
            magneticField > ferrousMagneticThreshold -> MaterialType.FERROUS_METAL
            electricField > nonFerrousElectricThreshold && 
            magneticField < nonFerrousMagneticThreshold -> MaterialType.NON_FERROUS_METAL
            electricField < 20f && magneticField < 5f -> MaterialType.CAVITY
            else -> MaterialType.UNKNOWN
        }
        
        // Berechne Zuverlässigkeit basierend auf Ähnlichkeit zu Referenzwerten
        val confidence = calculateConfidence(magneticField, electricField, baseType)
        
        return MaterialAnalysis(
            type = baseType,
            confidence = confidence,
            timestamp = measurement.timestamp
        )
    }

    private fun calculateConfidence(
        magneticField: Float,
        electricField: Float,
        type: MaterialType
    ): Float {
        // Finde die ähnlichste Referenzsignatur
        val closestSignature = REFERENCE_SIGNATURES.values.minByOrNull { signature ->
            val magneticDiff = (signature.magneticField - magneticField).absoluteValue
            val electricDiff = (signature.electricField - electricField).absoluteValue
            magneticDiff + electricDiff
        } ?: return 0f

        // Berechne Ähnlichkeit (0-1)
        val magneticSimilarity = 1f - (closestSignature.magneticField - magneticField)
            .absoluteValue / closestSignature.magneticField
        val electricSimilarity = 1f - (closestSignature.electricField - electricField)
            .absoluteValue / closestSignature.electricField

        return (magneticSimilarity + electricSimilarity) / 2f
    }

    fun getMaterialDescription(type: MaterialType): String {
        return when (type) {
            MaterialType.FERROUS_METAL -> "Eisenhaltiges Metall (z.B. Eisen, Stahl)"
            MaterialType.NON_FERROUS_METAL -> "Nicht-eisenhaltiges Metall (z.B. Kupfer, Aluminium)"
            MaterialType.CAVITY -> "Hohlraum oder nicht-metallisches Material"
            MaterialType.UNKNOWN -> "Unbekanntes Material"
        }
    }

    fun getCurrentThresholds(): Map<String, Float> {
        return mapOf(
            "Ferrous Magnetic" to ferrousMagneticThreshold,
            "Non-Ferrous Electric" to nonFerrousElectricThreshold,
            "Non-Ferrous Magnetic" to nonFerrousMagneticThreshold
        )
    }
} 