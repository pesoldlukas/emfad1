package com.emfad.app.models

import kotlin.math.sqrt
import kotlin.math.PI

data class MetalAnalysisResult(
    val metalType: MetalType,
    val conductivity: Double,
    val skinDepth: Double,
    val confidence: Double,
    val properties: MetalProperties
)

data class MetalProperties(
    val conductivity: Double,
    val permeability: Double,
    val density: Double,
    val color: String
)

enum class MetalType {
    GOLD,
    SILVER,
    COPPER,
    BRONZE,
    UNKNOWN
}

class MetalAnalyzer {
    companion object {
        private const val MU_0 = 4.0 * PI * 1e-7
        private const val MIN_CONFIDENCE = 0.7
        
        private val METAL_PROPERTIES = mapOf(
            MetalType.GOLD to MetalProperties(
                conductivity = 4.1e7,
                permeability = 1.0,
                density = 19.32,
                color = "Gelb"
            ),
            MetalType.SILVER to MetalProperties(
                conductivity = 6.3e7,
                permeability = 1.0,
                density = 10.49,
                color = "Silber"
            ),
            MetalType.COPPER to MetalProperties(
                conductivity = 5.8e7,
                permeability = 1.0,
                density = 8.96,
                color = "Rotbraun"
            ),
            MetalType.BRONZE to MetalProperties(
                conductivity = 1.0e6,
                permeability = 1.0,
                density = 8.73,
                color = "Bronze"
            )
        )
    }
    
    fun analyzeMetal(
        impedanceCurve: List<Pair<Double, Complex>>,
        frequency: Double
    ): MetalAnalysisResult {
        // 1. Berechne Skin-Tiefen
        val skinDepths = impedanceCurve.map { (freq, z) ->
            calculateSkinDepth(freq, z)
        }
        
        // 2. Bestimme Metalltyp
        val (metalType, properties) = determineMetalType(impedanceCurve, skinDepths)
        
        // 3. Berechne Zuverlässigkeit
        val confidence = calculateConfidence(impedanceCurve, skinDepths, properties)
        
        return MetalAnalysisResult(
            metalType = metalType,
            conductivity = properties.conductivity,
            skinDepth = skinDepths.first(),
            confidence = confidence,
            properties = properties
        )
    }
    
    private fun calculateSkinDepth(frequency: Double, impedance: Complex): Double {
        val omega = 2.0 * PI * frequency
        val conductivity = calculateConductivity(impedance, omega)
        return sqrt(2.0 / (MU_0 * conductivity * omega))
    }
    
    private fun calculateConductivity(z: Complex, omega: Double): Double {
        return (z.imag * omega * 8.854e-12) / (z.real * z.real + z.imag * z.imag)
    }
    
    private fun determineMetalType(
        impedanceCurve: List<Pair<Double, Complex>>,
        skinDepths: List<Double>
    ): Pair<MetalType, MetalProperties> {
        // Berechne durchschnittliche Leitfähigkeit
        val avgConductivity = impedanceCurve.map { (freq, z) ->
            calculateConductivity(z, 2.0 * PI * freq)
        }.average()
        
        // Bestimme Metalltyp basierend auf Leitfähigkeit
        val type = when {
            avgConductivity > 5.0e7 -> MetalType.SILVER
            avgConductivity > 4.0e7 -> MetalType.GOLD
            avgConductivity > 5.0e6 -> MetalType.COPPER
            avgConductivity > 1.0e6 -> MetalType.BRONZE
            else -> MetalType.UNKNOWN
        }
        
        return Pair(type, METAL_PROPERTIES[type] ?: METAL_PROPERTIES[MetalType.UNKNOWN]!!)
    }
    
    private fun calculateConfidence(
        impedanceCurve: List<Pair<Double, Complex>>,
        skinDepths: List<Double>,
        properties: MetalProperties
    ): Double {
        var confidence = 0.0
        
        // 1. Frequenzabhängigkeit (30%)
        val frequencyConfidence = calculateFrequencyConfidence(impedanceCurve)
        confidence += 0.3 * frequencyConfidence
        
        // 2. Skin-Effekt (30%)
        val skinEffectConfidence = calculateSkinEffectConfidence(skinDepths)
        confidence += 0.3 * skinEffectConfidence
        
        // 3. Leitfähigkeitsübereinstimmung (20%)
        val conductivityConfidence = calculateConductivityConfidence(
            impedanceCurve,
            properties.conductivity
        )
        confidence += 0.2 * conductivityConfidence
        
        // 4. Impedanzverlauf (20%)
        val impedanceConfidence = calculateImpedanceConfidence(impedanceCurve)
        confidence += 0.2 * impedanceConfidence
        
        return confidence.coerceIn(0.0, 1.0)
    }
    
    private fun calculateFrequencyConfidence(
        impedanceCurve: List<Pair<Double, Complex>>
    ): Double {
        // Prüfe auf erwartetes Frequenzverhalten
        val frequencies = impedanceCurve.map { it.first }
        val impedances = impedanceCurve.map { it.second.magnitude }
        
        // Berechne Korrelation zwischen Frequenz und Impedanz
        val correlation = calculateCorrelation(frequencies, impedances)
        
        return (correlation + 1.0) / 2.0 // Normalisiere auf [0,1]
    }
    
    private fun calculateSkinEffectConfidence(skinDepths: List<Double>): Double {
        // Prüfe auf erwartetes Skin-Effekt-Verhalten
        val expectedRatio = sqrt(skinDepths.first() / skinDepths.last())
        val actualRatio = skinDepths.first() / skinDepths.last()
        
        return (1.0 - abs(expectedRatio - actualRatio) / expectedRatio)
            .coerceIn(0.0, 1.0)
    }
    
    private fun calculateConductivityConfidence(
        impedanceCurve: List<Pair<Double, Complex>>,
        expectedConductivity: Double
    ): Double {
        val avgConductivity = impedanceCurve.map { (freq, z) ->
            calculateConductivity(z, 2.0 * PI * freq)
        }.average()
        
        val ratio = minOf(avgConductivity, expectedConductivity) /
                   maxOf(avgConductivity, expectedConductivity)
        
        return ratio.coerceIn(0.0, 1.0)
    }
    
    private fun calculateImpedanceConfidence(
        impedanceCurve: List<Pair<Double, Complex>>
    ): Double {
        // Prüfe auf erwarteten Impedanzverlauf
        val impedances = impedanceCurve.map { it.second.magnitude }
        val mean = impedances.average()
        val stdDev = sqrt(impedances.map { (it - mean).pow(2) }.average())
        
        return (1.0 - stdDev / mean).coerceIn(0.0, 1.0)
    }
    
    private fun calculateCorrelation(x: List<Double>, y: List<Double>): Double {
        val n = x.size
        val meanX = x.average()
        val meanY = y.average()
        
        val numerator = x.zip(y).sumOf { (xi, yi) ->
            (xi - meanX) * (yi - meanY)
        }
        
        val denominator = sqrt(
            x.sumOf { (it - meanX).pow(2) } *
            y.sumOf { (it - meanY).pow(2) }
        )
        
        return numerator / denominator
    }
} 