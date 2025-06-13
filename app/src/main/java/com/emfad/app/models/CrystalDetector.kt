package com.emfad.app.models

import kotlin.math.abs
import kotlin.math.sqrt

data class Complex(val real: Double, val imag: Double) {
    operator fun times(other: Complex) = Complex(
        real * other.real - imag * other.imag,
        real * other.imag + imag * other.real
    )
    
    operator fun div(other: Complex): Complex {
        val denom = other.real * other.real + other.imag * other.imag
        return Complex(
            (real * other.real + imag * other.imag) / denom,
            (imag * other.real - real * other.imag) / denom
        )
    }
    
    val magnitude: Double get() = sqrt(real * real + imag * imag)
}

data class CrystalDetectionResult(
    val isCrystal: Boolean,
    val confidence: Double,
    val permittivity: Complex,
    val impedanceDelta: Complex,
    val crystalType: CrystalType?
)

enum class CrystalType {
    RUBY, EMERALD, DIAMOND, TOURMALINE, QUARTZ
}

class CrystalDetector {
    companion object {
        private const val ALPHA_MIN = 1.5
        private const val ALPHA_MAX = 2.5
        private const val MU_0 = 4.0 * Math.PI * 1e-7
        private const val EPSILON_0 = 8.854e-12
    }
    
    private val crystalProperties = mapOf(
        CrystalType.RUBY to CrystalProperties(
            density = 3.9..4.1,
            conductivity = 0.0,
            permittivity = Complex(9.3, 0.0)..Complex(10.0, 0.0)
        ),
        CrystalType.EMERALD to CrystalProperties(
            density = 2.7..2.8,
            conductivity = 0.0,
            permittivity = Complex(6.0, 0.0)..Complex(8.0, 0.0)
        ),
        CrystalType.DIAMOND to CrystalProperties(
            density = 3.5..3.6,
            conductivity = 0.0,
            permittivity = Complex(5.5, 0.0)..Complex(7.0, 0.0)
        ),
        CrystalType.TOURMALINE to CrystalProperties(
            density = 3.0..3.3,
            conductivity = 0.0,
            permittivity = Complex(12.0, 0.0)..Complex(15.0, 0.0)
        ),
        CrystalType.QUARTZ to CrystalProperties(
            density = 2.6..2.7,
            conductivity = 0.0,
            permittivity = Complex(4.0, 0.0)..Complex(5.0, 0.0)
        )
    )
    
    fun detectCrystal(
        measuredZ: Complex,
        backgroundZ: Complex,
        frequency: Double,
        noiseStdDev: Double,
        alpha: Double = ALPHA_MIN
    ): CrystalDetectionResult {
        // 1. Berechne Impedanzänderung
        val impedanceDelta = measuredZ - backgroundZ
        
        // 2. Berechne lokale Permittivität
        val permittivity = calculateLocalPermittivity(impedanceDelta, frequency)
        
        // 3. Prüfe auf Kristall-Signatur
        val isCrystal = abs(impedanceDelta.magnitude) > alpha * noiseStdDev
        
        // 4. Bestimme Kristalltyp
        val crystalType = if (isCrystal) {
            identifyCrystalType(permittivity)
        } else null
        
        // 5. Berechne Zuverlässigkeit
        val confidence = calculateConfidence(impedanceDelta, noiseStdDev, crystalType)
        
        return CrystalDetectionResult(
            isCrystal = isCrystal,
            confidence = confidence,
            permittivity = permittivity,
            impedanceDelta = impedanceDelta,
            crystalType = crystalType
        )
    }
    
    private fun calculateLocalPermittivity(
        impedanceDelta: Complex,
        frequency: Double
    ): Complex {
        val omega = 2.0 * Math.PI * frequency
        val mu = MU_0 // Vereinfachung: konstante Permeabilität
        
        // Berechne lokale Permittivität aus Impedanzänderung
        val epsilonLocal = Complex(
            impedanceDelta.real * impedanceDelta.real + impedanceDelta.imag * impedanceDelta.imag,
            0.0
        ) * Complex(omega * mu / 2.0, 0.0)
        
        return epsilonLocal
    }
    
    private fun identifyCrystalType(permittivity: Complex): CrystalType? {
        return crystalProperties.entries.firstOrNull { (_, props) ->
            permittivity.real in props.permittivity.start.real..props.permittivity.endInclusive.real
        }?.key
    }
    
    private fun calculateConfidence(
        impedanceDelta: Complex,
        noiseStdDev: Double,
        crystalType: CrystalType?
    ): Double {
        var confidence = 0.0
        
        // 1. Signal-Rausch-Verhältnis (40%)
        val snr = impedanceDelta.magnitude / noiseStdDev
        confidence += 0.4 * (snr / 10.0).coerceIn(0.0, 1.0)
        
        // 2. Kristalltyp-Übereinstimmung (30%)
        if (crystalType != null) {
            confidence += 0.3
        }
        
        // 3. Permittivitäts-Bereich (30%)
        if (crystalType != null) {
            val props = crystalProperties[crystalType]!!
            val permittivityMatch = (impedanceDelta.magnitude in 
                props.permittivity.start.magnitude..props.permittivity.endInclusive.magnitude)
            confidence += 0.3 * if (permittivityMatch) 1.0 else 0.0
        }
        
        return confidence.coerceIn(0.0, 1.0)
    }
}

data class CrystalProperties(
    val density: ClosedRange<Double>,
    val conductivity: Double,
    val permittivity: ClosedRange<Complex>
) 