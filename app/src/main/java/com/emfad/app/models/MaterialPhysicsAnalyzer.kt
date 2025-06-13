package com.emfad.app.models

import kotlin.math.*

data class MaterialPhysicsAnalysis(
    val materialType: MaterialType,
    val depth: Float,  // Tiefe in mm
    val depthConfidence: Float,  // Zuverlässigkeit der Tiefenberechnung
    val size: Float,   // Größe in mm
    val conductivity: Float,  // Leitfähigkeit in S/m
    val confidence: Float,
    val magneticGradient: Float,  // nT/m
    val skinDepth: Float,  // mm
    val massEstimate: Float? = null,  // kg
    val volumeEstimate: Float? = null,  // m³
    val layerAnalysis: List<LayerAnalysis> = emptyList(),
    val gemstoneDetection: GemstoneDetection? = null,  // Edelstein-Erkennung
    val veinOrStructureDetection: VeinOrStructureDetection? = null  // Ader- oder Struktur-Erkennung
)

data class LayerAnalysis(
    val depth: Float,  // mm
    val material: String,
    val conductivity: Float,  // S/m
    val magneticGradient: Float,  // nT/m
    val dielectricConstant: Float,
    val density: Float  // g/cm³
)

class MaterialPhysicsAnalyzer {
    companion object {
        // Physikalische Konstanten
        private const val MU_0 = 4 * PI * 1e-7f  // Magnetische Feldkonstante
        private const val EPSILON_0 = 8.854e-12f  // Elektrische Feldkonstante
        private const val C = 299792458f  // Lichtgeschwindigkeit in m/s
        
        // Konversionsfaktoren
        private const val AB_TO_MICROVOLT = 0.48f  // 1 A/B-Einheit ≈ 0.48 µV/Hz
        private const val MAGNETIC_GRADIENT_FACTOR = 1.2f  // nT/m pro 1000 Einheiten
        
        // Referenzwerte für verschiedene Materialien
        private val MATERIAL_PROPERTIES = mapOf(
            MaterialType.FERROUS_METAL to MaterialProperties(
                relativePermeability = 1000f,
                conductivity = 1e7f,
                density = 7.87f,
                dielectricConstant = 1f,
                waveVelocity = C / sqrt(1000f)  // Wellengeschwindigkeit in ferromagnetischen Materialien
            ),
            MaterialType.NON_FERROUS_METAL to MaterialProperties(
                relativePermeability = 1f,
                conductivity = 5.8e7f,
                density = 19.3f,
                dielectricConstant = 1f,
                waveVelocity = C / sqrt(1f)
            ),
            MaterialType.CAVITY to MaterialProperties(
                relativePermeability = 1f,
                conductivity = 0f,
                density = 0f,
                dielectricConstant = 1f,
                waveVelocity = C
            )
        )
    }

    fun analyzeMaterial(
        magneticField: Double,
        electricField: Double,
        frequency: Double,
        phase: Double,
        depth: Double
    ): MaterialPhysicsAnalysis {
        // Berechne komplexe Impedanz
        val impedance = calculateImpedance(magneticField, electricField, phase)
        
        // Berechne Materialeigenschaften
        val conductivity = calculateConductivity(impedance, frequency)
        val permittivity = calculatePermittivity(impedance, frequency)
        val permeability = calculatePermeability(impedance, frequency)
        
        // Berechne magnetischen Gradienten
        val magneticGradient = calculateMagneticGradient(magneticField, depth)
        
        // Bestimme Anomalieform
        val anomalyShape = determineAnomalyShape(
            magneticField,
            electricField,
            phase,
            depth
        )
        
        // Berechne Aspektverhältnis und Symmetrie
        val aspectRatio = calculateAspectRatio(magneticField, electricField, depth)
        val symmetry = calculateSymmetry(magneticField, electricField, phase)
        
        // Prüfe auf Edelstein
        val gemstoneDetection = MaterialDatabase.detectGemstone(
            conductivity,
            permittivity,
            permeability,
            magneticGradient,
            anomalyShape
        )
        
        // Prüfe auf Ader oder künstliche Struktur
        val veinOrStructureDetection = MaterialDatabase.detectVeinOrStructure(
            conductivity,
            permittivity,
            permeability,
            magneticGradient,
            anomalyShape,
            aspectRatio,
            symmetry,
            depth
        )
        
        // Finde passendes Material
        val material = MaterialDatabase.findMaterialByProperties(
            conductivity,
            permittivity,
            permeability
        )
        
        // Berechne Skintiefe
        val skinDepth = material?.let { 
            MaterialDatabase.calculateSkinDepth(frequency, it)
        } ?: calculateEstimatedSkinDepth(conductivity, frequency)
        
        // Bestimme Materialtyp
        val materialType = determineMaterialType(
            conductivity,
            permittivity,
            permeability,
            phase
        )
        
        // Berechne Volumen und Masse
        val (volume, mass) = calculateVolumeAndMass(
            depth,
            materialType,
            material?.density ?: estimateDensity(materialType)
        )
        
        // Analysiere Schichtung
        val layers = analyzeLayers(
            magneticField,
            electricField,
            frequency,
            phase,
            depth
        )
        
        return MaterialPhysicsAnalysis(
            materialType = materialType,
            depth = depth.toFloat(),
            depthConfidence = calculateDepthConfidence(
                magneticField,
                electricField,
                phase,
                depth
            ),
            size = volume.toFloat(),
            conductivity = conductivity.toFloat(),
            confidence = calculateReliability(
                magneticField,
                electricField,
                phase,
                materialType
            ),
            magneticGradient = magneticGradient.toFloat(),
            skinDepth = skinDepth.toFloat(),
            massEstimate = mass?.toFloat(),
            volumeEstimate = volume.toFloat(),
            layerAnalysis = layers,
            gemstoneDetection = gemstoneDetection,
            veinOrStructureDetection = veinOrStructureDetection
        )
    }
    
    private fun calculateImpedance(
        magneticField: Double,
        electricField: Double,
        phase: Double
    ): Complex {
        val magnitude = electricField / magneticField
        return Complex(
            magnitude * cos(phase),
            magnitude * sin(phase)
        )
    }
    
    private fun calculateConductivity(
        impedance: Complex,
        frequency: Double
    ): Double {
        val omega = 2 * PI * frequency
        val mu0 = 4 * PI * 1e-7
        
        // σ = ω * μ₀ * |Z|² / (|Z|² + 2 * Re(Z) * |Z| + Re(Z)²)
        val magnitudeSquared = impedance.real * impedance.real + impedance.imaginary * impedance.imaginary
        val denominator = magnitudeSquared + 2 * impedance.real * sqrt(magnitudeSquared) + impedance.real * impedance.real
        
        return omega * mu0 * magnitudeSquared / denominator
    }
    
    private fun calculatePermittivity(
        impedance: Complex,
        frequency: Double
    ): Double {
        val omega = 2 * PI * frequency
        val epsilon0 = 8.854e-12
        
        // εr = -Im(Z) / (ω * ε₀ * |Z|²)
        val magnitudeSquared = impedance.real * impedance.real + impedance.imaginary * impedance.imaginary
        return -impedance.imaginary / (omega * epsilon0 * magnitudeSquared)
    }
    
    private fun calculatePermeability(
        impedance: Complex,
        frequency: Double
    ): Double {
        val omega = 2 * PI * frequency
        val mu0 = 4 * PI * 1e-7
        
        // μr = Re(Z) / (ω * μ₀)
        return impedance.real / (omega * mu0)
    }
    
    private fun determineMaterialType(
        conductivity: Double,
        permittivity: Double,
        permeability: Double,
        phase: Double
    ): MaterialType {
        return when {
            conductivity > 1e6 -> MaterialType.FERROUS_METAL
            conductivity > 1e4 -> MaterialType.NON_FERROUS_METAL
            permittivity > 50 -> MaterialType.WATER
            phase < -PI/4 -> MaterialType.CAVITY
            else -> MaterialType.UNKNOWN
        }
    }
    
    private fun calculateVolumeAndMass(
        depth: Double,
        materialType: MaterialType,
        density: Double
    ): Pair<Double, Double> {
        // Schätze Volumen basierend auf Tiefe und Materialtyp
        val volume = when (materialType) {
            MaterialType.FERROUS_METAL -> depth * depth * depth * 0.5
            MaterialType.NON_FERROUS_METAL -> depth * depth * depth * 0.3
            MaterialType.CAVITY -> depth * depth * depth * 0.8
            MaterialType.WATER -> depth * depth * depth * 0.6
            else -> depth * depth * depth * 0.4
        }
        
        val mass = volume * density
        return Pair(volume, mass)
    }
    
    private fun analyzeLayers(
        magneticField: Double,
        electricField: Double,
        frequency: Double,
        phase: Double,
        depth: Double
    ): List<LayerAnalysis> {
        val layers = mutableListOf<LayerAnalysis>()
        
        // Analysiere Schichtung durch Fourier-Transformation
        val frequencies = listOf(frequency/2, frequency, frequency*2)
        val phases = frequencies.map { freq ->
            calculatePhaseAtFrequency(magneticField, electricField, freq)
        }
        
        // Erkenne Schichtgrenzen durch Phasensprünge
        for (i in 0 until phases.size - 1) {
            val phaseDiff = abs(phases[i+1] - phases[i])
            if (phaseDiff > PI/6) {
                val layerDepth = depth * (i+1) / frequencies.size
                val layerMaterial = determineMaterialType(
                    calculateConductivity(calculateImpedance(magneticField, electricField, phases[i]), frequencies[i]),
                    calculatePermittivity(calculateImpedance(magneticField, electricField, phases[i]), frequencies[i]),
                    calculatePermeability(calculateImpedance(magneticField, electricField, phases[i]), frequencies[i]),
                    phases[i]
                )
                
                layers.add(LayerAnalysis(
                    materialType = layerMaterial,
                    depth = layerDepth,
                    reliability = calculateLayerReliability(phaseDiff)
                ))
            }
        }
        
        return layers
    }
    
    private fun analyzeAnomalies(
        magneticField: Double,
        electricField: Double,
        frequency: Double,
        phase: Double,
        depth: Double
    ): List<AnomalyAnalysis> {
        val anomalies = mutableListOf<AnomalyAnalysis>()
        
        // Analysiere Anomalien durch Gradientenanalyse
        val magneticGradient = calculateMagneticGradient(magneticField, depth)
        val electricGradient = calculateElectricGradient(electricField, depth)
        
        // Erkenne Anomalien durch Gradientenänderungen
        if (abs(magneticGradient) > 0.1 || abs(electricGradient) > 0.1) {
            val anomalyType = when {
                magneticGradient > 0.1 && electricGradient < 0.1 -> AnomalyType.MAGNETIC
                electricGradient > 0.1 && magneticGradient < 0.1 -> AnomalyType.ELECTRIC
                else -> AnomalyType.COMBINED
            }
            
            anomalies.add(AnomalyAnalysis(
                type = anomalyType,
                intensity = max(abs(magneticGradient), abs(electricGradient)),
                depth = depth,
                reliability = calculateAnomalyReliability(magneticGradient, electricGradient)
            ))
        }
        
        return anomalies
    }
    
    private fun calculateMagneticGradient(magneticField: Double, depth: Double): Double {
        // Berechne magnetischen Gradienten
        return magneticField / (depth * depth)
    }
    
    private fun calculateElectricGradient(electricField: Double, depth: Double): Double {
        // Berechne elektrischen Gradienten
        return electricField / (depth * depth)
    }
    
    private fun calculatePhaseAtFrequency(
        magneticField: Double,
        electricField: Double,
        frequency: Double
    ): Double {
        // Berechne Phase bei gegebener Frequenz
        val impedance = calculateImpedance(magneticField, electricField, 0.0)
        return atan2(impedance.imaginary, impedance.real)
    }
    
    private fun calculateLayerReliability(phaseDiff: Double): Double {
        // Berechne Zuverlässigkeit der Schichtanalyse
        return min(1.0, phaseDiff / (PI/2))
    }
    
    private fun calculateAnomalyReliability(
        magneticGradient: Double,
        electricGradient: Double
    ): Double {
        // Berechne Zuverlässigkeit der Anomalieanalyse
        val maxGradient = max(abs(magneticGradient), abs(electricGradient))
        return min(1.0, maxGradient / 0.5)
    }
    
    private fun calculateEstimatedSkinDepth(conductivity: Double, frequency: Double): Double {
        val omega = 2 * PI * frequency
        val mu0 = 4 * PI * 1e-7
        return sqrt(2.0 / (omega * mu0 * conductivity))
    }
    
    private fun estimateDensity(materialType: MaterialType): Double {
        return when (materialType) {
            MaterialType.FERROUS_METAL -> 7.8
            MaterialType.NON_FERROUS_METAL -> 8.9
            MaterialType.CAVITY -> 0.0
            MaterialType.WATER -> 1.0
            else -> 2.5
        }
    }
    
    private fun calculateReliability(
        magneticField: Double,
        electricField: Double,
        phase: Double,
        materialType: MaterialType
    ): Double {
        // Berechne Gesamtzuverlässigkeit der Analyse
        val fieldRatio = min(magneticField, electricField) / max(magneticField, electricField)
        val phaseReliability = 1.0 - abs(phase) / PI
        val typeReliability = when (materialType) {
            MaterialType.FERROUS_METAL -> 0.9
            MaterialType.NON_FERROUS_METAL -> 0.8
            MaterialType.CAVITY -> 0.7
            MaterialType.WATER -> 0.85
            else -> 0.5
        }
        
        return (fieldRatio + phaseReliability + typeReliability) / 3.0
    }
    
    private fun determineAnomalyShape(
        magneticField: Double,
        electricField: Double,
        phase: Double,
        depth: Double
    ): AnomalyShape {
        // Berechne Gradienten in verschiedenen Richtungen
        val horizontalGradient = calculateHorizontalGradient(magneticField, depth)
        val verticalGradient = calculateVerticalGradient(magneticField, depth)
        val phaseGradient = calculatePhaseGradient(phase, depth)
        
        // Bestimme Form basierend auf Gradienten
        return when {
            // Spalt: Starker vertikaler Gradient, schwacher horizontaler
            verticalGradient > 0.5 && horizontalGradient < 0.2 -> AnomalyShape.CREVICE
            
            // Punkt: Gleichmäßige Gradienten in alle Richtungen
            abs(verticalGradient - horizontalGradient) < 0.1 -> AnomalyShape.POINT
            
            // Ader: Starker horizontaler Gradient, schwacher vertikaler
            horizontalGradient > 0.5 && verticalGradient < 0.2 -> AnomalyShape.VEIN
            
            // Hohlraum: Starke Phasenänderung, schwache Gradienten
            abs(phaseGradient) > 0.5 && 
            verticalGradient < 0.2 && 
            horizontalGradient < 0.2 -> AnomalyShape.CAVITY
            
            // Schicht: Gleichmäßige Gradienten mit starker Phasenänderung
            abs(verticalGradient - horizontalGradient) < 0.2 && 
            abs(phaseGradient) > 0.3 -> AnomalyShape.LAYER
            
            // Unbekannt
            else -> AnomalyShape.UNKNOWN
        }
    }
    
    private fun calculateHorizontalGradient(magneticField: Double, depth: Double): Double {
        // Berechne horizontalen Gradienten
        return magneticField / (depth * depth)
    }
    
    private fun calculateVerticalGradient(magneticField: Double, depth: Double): Double {
        // Berechne vertikalen Gradienten
        return magneticField / (depth * depth * depth)
    }
    
    private fun calculatePhaseGradient(phase: Double, depth: Double): Double {
        // Berechne Phasengradienten
        return phase / depth
    }
    
    private fun calculateDepthConfidence(
        magneticField: Double,
        electricField: Double,
        phase: Double,
        depth: Double
    ): Float {
        // Berechne Zuverlässigkeit der Tiefenbestimmung
        val fieldRatio = min(magneticField, electricField) / max(magneticField, electricField)
        val phaseReliability = 1.0 - abs(phase) / PI
        val depthReliability = when {
            depth < 100 -> 0.9  // Sehr zuverlässig bei geringer Tiefe
            depth < 500 -> 0.8  // Gut bei mittlerer Tiefe
            depth < 1000 -> 0.6 // Mäßig bei größerer Tiefe
            else -> 0.4         // Weniger zuverlässig bei großer Tiefe
        }
        
        return ((fieldRatio + phaseReliability + depthReliability) / 3.0).toFloat()
    }
    
    private fun calculateAspectRatio(
        magneticField: Double,
        electricField: Double,
        depth: Double
    ): Double {
        // Berechne das Aspektverhältnis basierend auf den Gradienten
        val horizontalGradient = calculateHorizontalGradient(magneticField, depth)
        val verticalGradient = calculateVerticalGradient(magneticField, depth)
        
        return if (verticalGradient != 0.0) {
            horizontalGradient / verticalGradient
        } else {
            1.0
        }
    }
    
    private fun calculateSymmetry(
        magneticField: Double,
        electricField: Double,
        phase: Double
    ): Double {
        // Berechne die Symmetrie basierend auf den Feldverteilungen
        val fieldRatio = min(magneticField, electricField) / max(magneticField, electricField)
        val phaseSymmetry = 1.0 - abs(phase) / PI
        
        return (fieldRatio + phaseSymmetry) / 2.0
    }
}

private data class MaterialProperties(
    val relativePermeability: Float,
    val conductivity: Float,
    val density: Float,
    val dielectricConstant: Float,
    val waveVelocity: Float
) 