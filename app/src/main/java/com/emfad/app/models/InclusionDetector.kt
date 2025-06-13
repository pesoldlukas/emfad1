package com.emfad.app.models

import kotlin.math.tan
import kotlin.math.PI

data class InclusionDetectionResult(
    val hasInclusion: Boolean,
    val inclusionType: InclusionType,
    val depth: Double,
    val size: Double,
    val confidence: Double,
    val properties: InclusionProperties
)

data class InclusionProperties(
    val conductivity: Double,
    val permittivity: Complex,
    val permeability: Double,
    val density: Double
)

enum class InclusionType {
    METAL,
    CRYSTAL,
    VOID,
    UNKNOWN
}

class InclusionDetector {
    companion object {
        private const val MU_0 = 4.0 * PI * 1e-7
        private const val EPSILON_0 = 8.854e-12
        private const val MIN_CONFIDENCE = 0.7
    }
    
    fun detectInclusion(
        measuredZ: Complex,
        frequency: Double,
        depth: Double,
        surroundingMaterial: MaterialProperties
    ): InclusionDetectionResult {
        // 1. Berechne effektive Impedanz
        val effectiveZ = calculateEffectiveImpedance(measuredZ, frequency, depth)
        
        // 2. Bestimme Einschlusstyp
        val (inclusionType, properties) = determineInclusionType(effectiveZ, frequency)
        
        // 3. Berechne Größe
        val size = estimateInclusionSize(effectiveZ, depth, properties)
        
        // 4. Berechne Zuverlässigkeit
        val confidence = calculateConfidence(
            effectiveZ,
            surroundingMaterial,
            properties,
            depth
        )
        
        return InclusionDetectionResult(
            hasInclusion = confidence > MIN_CONFIDENCE,
            inclusionType = inclusionType,
            depth = depth,
            size = size,
            confidence = confidence,
            properties = properties
        )
    }
    
    private fun calculateEffectiveImpedance(
        measuredZ: Complex,
        frequency: Double,
        depth: Double
    ): Complex {
        val omega = 2.0 * PI * frequency
        val k = omega * sqrt(MU_0 * EPSILON_0)
        
        // Berechne effektive Impedanz nach Mehrschicht-Modell
        val Z1 = Complex(377.0, 0.0) // Luft-Impedanz
        val Z2 = measuredZ
        
        val k2d = k * depth
        val tanK2d = tan(k2d)
        
        return (Z1 + Complex(0.0, Z2.magnitude * tanK2d)) /
               (Complex(1.0, 0.0) + Complex(0.0, Z2.magnitude / Z1.magnitude * tanK2d))
    }
    
    private fun determineInclusionType(
        effectiveZ: Complex,
        frequency: Double
    ): Pair<InclusionType, InclusionProperties> {
        val omega = 2.0 * PI * frequency
        
        // Berechne Materialeigenschaften aus Impedanz
        val conductivity = calculateConductivity(effectiveZ, omega)
        val permittivity = calculatePermittivity(effectiveZ, omega)
        val permeability = calculatePermeability(effectiveZ, omega)
        val density = estimateDensity(conductivity, permittivity)
        
        val properties = InclusionProperties(
            conductivity = conductivity,
            permittivity = permittivity,
            permeability = permeability,
            density = density
        )
        
        val type = when {
            // Metall
            conductivity > 1e6 -> InclusionType.METAL
            
            // Kristall
            conductivity < 1e-10 && permittivity.magnitude > 5.0 -> InclusionType.CRYSTAL
            
            // Hohlraum
            conductivity < 1e-12 && permittivity.magnitude < 1.1 -> InclusionType.VOID
            
            else -> InclusionType.UNKNOWN
        }
        
        return Pair(type, properties)
    }
    
    private fun calculateConductivity(z: Complex, omega: Double): Double {
        return (z.imag * omega * EPSILON_0) / (z.real * z.real + z.imag * z.imag)
    }
    
    private fun calculatePermittivity(z: Complex, omega: Double): Complex {
        val sigma = calculateConductivity(z, omega)
        return Complex(
            z.real / (omega * MU_0),
            -sigma / (omega * EPSILON_0)
        )
    }
    
    private fun calculatePermeability(z: Complex, omega: Double): Double {
        return (z.real * z.real + z.imag * z.imag) / (omega * MU_0)
    }
    
    private fun estimateDensity(conductivity: Double, permittivity: Complex): Double {
        // Vereinfachte Schätzung basierend auf Materialeigenschaften
        return when {
            conductivity > 1e6 -> 8.0..10.0 // Metall
            conductivity < 1e-10 && permittivity.magnitude > 5.0 -> 2.5..4.0 // Kristall
            else -> 1.0..2.0 // Hohlraum
        }.random()
    }
    
    private fun estimateInclusionSize(
        effectiveZ: Complex,
        depth: Double,
        properties: InclusionProperties
    ): Double {
        // Größenabschätzung basierend auf Impedanz und Tiefe
        val impedanceRatio = effectiveZ.magnitude / 377.0 // Normierung auf Luft-Impedanz
        return depth * impedanceRatio * (properties.permittivity.magnitude / 10.0)
    }
    
    private fun calculateConfidence(
        effectiveZ: Complex,
        surroundingMaterial: MaterialProperties,
        inclusionProperties: InclusionProperties,
        depth: Double
    ): Double {
        var confidence = 0.0
        
        // 1. Impedanzkontrast (30%)
        val impedanceContrast = abs(effectiveZ.magnitude - 377.0) / 377.0
        confidence += 0.3 * impedanceContrast.coerceIn(0.0, 1.0)
        
        // 2. Materialkontrast (30%)
        val materialContrast = calculateMaterialContrast(
            surroundingMaterial,
            inclusionProperties
        )
        confidence += 0.3 * materialContrast
        
        // 3. Tiefenabhängigkeit (20%)
        val depthConfidence = (1.0 - (depth / 10.0)).coerceIn(0.0, 1.0)
        confidence += 0.2 * depthConfidence
        
        // 4. Signalstärke (20%)
        val signalStrength = effectiveZ.magnitude / 1000.0
        confidence += 0.2 * signalStrength.coerceIn(0.0, 1.0)
        
        return confidence.coerceIn(0.0, 1.0)
    }
    
    private fun calculateMaterialContrast(
        surrounding: MaterialProperties,
        inclusion: InclusionProperties
    ): Double {
        val conductivityContrast = abs(
            log10(surrounding.conductivity) - log10(inclusion.conductivity)
        ).coerceIn(0.0, 10.0) / 10.0
        
        val permittivityContrast = abs(
            surrounding.permittivity.magnitude - inclusion.permittivity.magnitude
        ).coerceIn(0.0, 10.0) / 10.0
        
        return (conductivityContrast + permittivityContrast) / 2.0
    }
    
    private fun log10(x: Double): Double = kotlin.math.ln(x) / kotlin.math.ln(10.0)
} 