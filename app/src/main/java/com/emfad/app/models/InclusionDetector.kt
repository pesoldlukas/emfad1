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
    val permittivity: Double,
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
        // 1. Calculate effective impedance
        val effectiveZ = calculateEffectiveImpedance(measuredZ, frequency, depth)
        
        // 2. Determine inclusion type
        val (inclusionType, properties) = determineInclusionType(effectiveZ, frequency)
        
        // 3. Estimate size
        val size = estimateInclusionSize(effectiveZ, depth, properties)
        
        // 4. Calculate confidence
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
        
        // Calculate effective impedance using multilayer model
        val Z1 = Complex(377.0, 0.0) // Air impedance
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
        
        // Calculate material properties from impedance
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
            // Metal
            conductivity > 1e6 -> InclusionType.METAL
            
            // Crystal
            conductivity < 1e-10 && permittivity > 5.0 -> InclusionType.CRYSTAL
            
            // Void
            conductivity < 1e-12 && permittivity < 1.1 -> InclusionType.VOID
            
            else -> InclusionType.UNKNOWN
        }
        
        return Pair(type, properties)
    }
    
    private fun calculateConductivity(z: Complex, omega: Double): Double {
        return (z.imag * omega * EPSILON_0) / (z.real * z.real + z.imag * z.imag)
    }
    
    private fun calculatePermittivity(z: Complex, omega: Double): Double {
        return z.real / (omega * MU_0)
    }
    
    private fun calculatePermeability(z: Complex, omega: Double): Double {
        return (z.real * z.real + z.imag * z.imag) / (omega * MU_0)
    }
    
    private fun estimateDensity(conductivity: Double, permittivity: Double): Double {
        // Simplified estimation based on material properties
        return when {
            conductivity > 1e6 -> 8.0 // Metal
            conductivity < 1e-10 && permittivity > 5.0 -> 3.0 // Crystal
            else -> 1.0 // Void
        }
    }
    
    private fun estimateInclusionSize(
        effectiveZ: Complex,
        depth: Double,
        properties: InclusionProperties
    ): Double {
        // Size estimation based on impedance and depth
        val impedanceRatio = effectiveZ.magnitude / 377.0 // Normalized to air impedance
        return depth * impedanceRatio * (properties.permittivity / 10.0)
    }
    
    private fun calculateConfidence(
        effectiveZ: Complex,
        surroundingMaterial: MaterialProperties,
        inclusionProperties: InclusionProperties,
        depth: Double
    ): Double {
        var confidence = 0.0
        
        // 1. Impedance contrast (30%)
        val impedanceContrast = abs(effectiveZ.magnitude - 377.0) / 377.0
        confidence += 0.3 * impedanceContrast.coerceIn(0.0, 1.0)
        
        // 2. Material contrast (30%)
        val materialContrast = calculateMaterialContrast(
            surroundingMaterial,
            inclusionProperties
        )
        confidence += 0.3 * materialContrast
        
        // 3. Depth dependency (20%)
        val depthConfidence = (1.0 - (depth / 10.0)).coerceIn(0.0, 1.0)
        confidence += 0.2 * depthConfidence
        
        // 4. Signal strength (20%)
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
            surrounding.permittivity - inclusion.permittivity
        ).coerceIn(0.0, 10.0) / 10.0
        
        return (conductivityContrast + permittivityContrast) / 2.0
    }
    
    private fun log10(x: Double): Double = kotlin.math.ln(x) / kotlin.math.ln(10.0)
}

data class MaterialProperties(
    val conductivity: Double,
    val permittivity: Double,
    val permeability: Double
)
