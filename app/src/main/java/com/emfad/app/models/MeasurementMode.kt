package com.emfad.app.models

import kotlin.math.PI
import kotlin.math.sqrt

enum class MeasurementMode {
    BA_VERTICAL,
    AB_HORIZONTAL,
    ANTENNA_A,
    DEPTH_PRO
}

data class MeasurementConfig(
    val mode: MeasurementMode,
    val frequency: Double,
    val antennaDistance: Double,
    val calibrationFactor: Double = 1.0,
    val isCalibrated: Boolean = false
)

data class MeasurementResult(
    val impedance: Complex,
    val depth: Double,
    val confidence: Double,
    val mode: MeasurementMode,
    val timestamp: Long = System.currentTimeMillis()
)

class MeasurementController {
    companion object {
        private const val MU_0 = 4.0 * PI * 1e-7
        private const val EPSILON_0 = 8.854e-12
        
        // Standardkonfigurationen für jeden Modus
        private val DEFAULT_CONFIGS = mapOf(
            MeasurementMode.BA_VERTICAL to MeasurementConfig(
                mode = MeasurementMode.BA_VERTICAL,
                frequency = 1000.0, // 1 kHz
                antennaDistance = 1.0 // 1m
            ),
            MeasurementMode.AB_HORIZONTAL to MeasurementConfig(
                mode = MeasurementMode.AB_HORIZONTAL,
                frequency = 2000.0, // 2 kHz
                antennaDistance = 0.5 // 0.5m
            ),
            MeasurementMode.ANTENNA_A to MeasurementConfig(
                mode = MeasurementMode.ANTENNA_A,
                frequency = 500.0, // 500 Hz
                antennaDistance = 0.0 // Einzelantenne
            ),
            MeasurementMode.DEPTH_PRO to MeasurementConfig(
                mode = MeasurementMode.DEPTH_PRO,
                frequency = 5000.0, // 5 kHz
                antennaDistance = 1.5 // 1.5m
            )
        )
    }
    
    private var currentConfig: MeasurementConfig = DEFAULT_CONFIGS[MeasurementMode.BA_VERTICAL]!!
    
    fun setMode(mode: MeasurementMode) {
        currentConfig = DEFAULT_CONFIGS[mode] ?: throw IllegalArgumentException("Ungültiger Messmodus")
    }
    
    fun updateConfig(config: MeasurementConfig) {
        currentConfig = config
    }
    
    fun measure(): MeasurementResult {
        // Simuliere Messung basierend auf aktuellem Modus
        val (impedance, depth) = when (currentConfig.mode) {
            MeasurementMode.BA_VERTICAL -> measureVertical()
            MeasurementMode.AB_HORIZONTAL -> measureHorizontal()
            MeasurementMode.ANTENNA_A -> measureSingleAntenna()
            MeasurementMode.DEPTH_PRO -> measureDepthPro()
        }
        
        return MeasurementResult(
            impedance = impedance,
            depth = depth,
            confidence = calculateConfidence(impedance, depth),
            mode = currentConfig.mode
        )
    }
    
    private fun measureVertical(): Pair<Complex, Double> {
        // B-A Vertikal Messung
        val omega = 2.0 * PI * currentConfig.frequency
        val k = omega * sqrt(MU_0 * EPSILON_0)
        
        // Simuliere vertikale Impedanz
        val z = Complex(
            real = 377.0 * (1.0 + 0.1 * kotlin.math.sin(k * currentConfig.antennaDistance)),
            imag = 377.0 * 0.1 * kotlin.math.cos(k * currentConfig.antennaDistance)
        )
        
        // Schätze Tiefe basierend auf Impedanz
        val depth = estimateDepth(z, currentConfig.frequency)
        
        return Pair(z, depth)
    }
    
    private fun measureHorizontal(): Pair<Complex, Double> {
        // A-B Horizontal Messung
        val omega = 2.0 * PI * currentConfig.frequency
        val k = omega * sqrt(MU_0 * EPSILON_0)
        
        // Simuliere horizontale Impedanz
        val z = Complex(
            real = 377.0 * (1.0 + 0.2 * kotlin.math.cos(k * currentConfig.antennaDistance)),
            imag = 377.0 * 0.2 * kotlin.math.sin(k * currentConfig.antennaDistance)
        )
        
        // Schätze Tiefe basierend auf Impedanz
        val depth = estimateDepth(z, currentConfig.frequency)
        
        return Pair(z, depth)
    }
    
    private fun measureSingleAntenna(): Pair<Complex, Double> {
        // Antenne A Messung
        val omega = 2.0 * PI * currentConfig.frequency
        
        // Simuliere Einzelantennen-Impedanz
        val z = Complex(
            real = 377.0 * (1.0 + 0.05 * kotlin.math.sin(omega * 0.001)),
            imag = 377.0 * 0.05 * kotlin.math.cos(omega * 0.001)
        )
        
        // Schätze Tiefe basierend auf Impedanz
        val depth = estimateDepth(z, currentConfig.frequency)
        
        return Pair(z, depth)
    }
    
    private fun measureDepthPro(): Pair<Complex, Double> {
        // Tiefe Pro Messung (Hz/Hx-Methode)
        val omega = 2.0 * PI * currentConfig.frequency
        val k = omega * sqrt(MU_0 * EPSILON_0)
        
        // Simuliere Hz/Hx-Messung
        val hz = Complex(
            real = kotlin.math.sin(k * currentConfig.antennaDistance),
            imag = kotlin.math.cos(k * currentConfig.antennaDistance)
        )
        
        val hx = Complex(
            real = kotlin.math.cos(k * currentConfig.antennaDistance),
            imag = kotlin.math.sin(k * currentConfig.antennaDistance)
        )
        
        val z = hz / hx * 377.0
        
        // Präzise Tiefenschätzung für Tiefe Pro
        val depth = estimateDepthPro(z, hz, hx)
        
        return Pair(z, depth)
    }
    
    private fun estimateDepth(z: Complex, frequency: Double): Double {
        // Vereinfachte Tiefenschätzung basierend auf Impedanz und Frequenz
        val omega = 2.0 * PI * frequency
        val k = omega * sqrt(MU_0 * EPSILON_0)
        
        return kotlin.math.abs(z.imag) / (k * z.real)
    }
    
    private fun estimateDepthPro(z: Complex, hz: Complex, hx: Complex): Double {
        // Präzise Tiefenschätzung nach Hz/Hx-Methode
        val phaseDiff = kotlin.math.atan2(hz.imag, hz.real) - kotlin.math.atan2(hx.imag, hx.real)
        val k = 2.0 * PI * currentConfig.frequency * sqrt(MU_0 * EPSILON_0)
        
        return phaseDiff / k
    }
    
    private fun calculateConfidence(z: Complex, depth: Double): Double {
        var confidence = 0.0
        
        // 1. Signalstärke (30%)
        val signalStrength = z.magnitude / 377.0
        confidence += 0.3 * signalStrength.coerceIn(0.0, 1.0)
        
        // 2. Tiefenabhängigkeit (30%)
        val depthConfidence = when (currentConfig.mode) {
            MeasurementMode.BA_VERTICAL -> (1.0 - (depth / 5.0)).coerceIn(0.0, 1.0)
            MeasurementMode.AB_HORIZONTAL -> (1.0 - (depth / 3.0)).coerceIn(0.0, 1.0)
            MeasurementMode.ANTENNA_A -> (1.0 - (depth / 2.0)).coerceIn(0.0, 1.0)
            MeasurementMode.DEPTH_PRO -> (1.0 - (depth / 10.0)).coerceIn(0.0, 1.0)
        }
        confidence += 0.3 * depthConfidence
        
        // 3. Kalibrierungsqualität (20%)
        val calibrationConfidence = if (currentConfig.isCalibrated) 1.0 else 0.5
        confidence += 0.2 * calibrationConfidence
        
        // 4. Frequenzabhängigkeit (20%)
        val frequencyConfidence = (currentConfig.frequency / 5000.0).coerceIn(0.0, 1.0)
        confidence += 0.2 * frequencyConfidence
        
        return confidence.coerceIn(0.0, 1.0)
    }
} 