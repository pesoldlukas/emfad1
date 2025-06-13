package com.emfad.app.models

data class MaterialProperties(
    val name: String,
    val conductivity: Double,        // Leitfähigkeit in S/m
    val relativePermittivity: Double, // Relative Permittivität
    val relativePermeability: Double, // Relative Permeabilität
    val density: Double,             // Dichte in g/cm³
    val color: Int,                  // Materialfarbe für Visualisierung
    val reflectionCoefficient: Double, // Reflexionskoeffizient
    val isGemstone: Boolean = false,  // Ist es ein Edelstein?
    val gemstoneType: GemstoneType? = null, // Typ des Edelsteins, falls vorhanden
    val isNaturalVein: Boolean = false, // Ist es eine natürliche Ader?
    val veinType: VeinType? = null,  // Typ der Ader, falls vorhanden
    val isArtificialStructure: Boolean = false, // Ist es eine künstliche Struktur?
    val structureType: StructureType? = null // Typ der Struktur, falls vorhanden
)

enum class GemstoneType {
    DIAMOND,    // Diamant
    RUBY,       // Rubin
    EMERALD,    // Smaragd
    SAPPHIRE,   // Saphir
    TOPAZ,      // Topas
    AMETHYST,   // Amethyst
    TOURMALINE, // Turmalin
    GARNET      // Granat
}

enum class VeinType {
    GOLD_VEIN,      // Goldader
    SILVER_VEIN,    // Silberader
    COPPER_VEIN,    // Kupferader
    QUARTZ_VEIN,    // Quarzader
    PYRITE_VEIN,    // Pyritader
    MAGNETITE_VEIN  // Magnetitader
}

enum class StructureType {
    TUNNEL,         // Tunnel
    CHAMBER,        // Kammer
    WELL,           // Brunnen
    FOUNDATION,     // Fundament
    WALL,           // Mauer
    DRAINAGE        // Drainage
}

object MaterialDatabase {
    val materials = mapOf(
        "GOLD" to MaterialProperties(
            name = "Gold",
            conductivity = 4.1e7,
            relativePermittivity = 1.0,
            relativePermeability = 1.0,
            density = 19.3,
            color = 0xFFFFD700,
            reflectionCoefficient = 0.95
        ),
        "COPPER" to MaterialProperties(
            name = "Kupfer",
            conductivity = 5.8e7,
            relativePermittivity = 1.0,
            relativePermeability = 1.0,
            density = 8.96,
            color = 0xFFB87333,
            reflectionCoefficient = 0.90
        ),
        "ALUMINUM" to MaterialProperties(
            name = "Aluminium",
            conductivity = 3.5e7,
            relativePermittivity = 1.0,
            relativePermeability = 1.0,
            density = 2.70,
            color = 0xFFA9A9A9,
            reflectionCoefficient = 0.85
        ),
        "SILVER" to MaterialProperties(
            name = "Silber",
            conductivity = 6.3e7,
            relativePermittivity = 1.0,
            relativePermeability = 1.0,
            density = 10.49,
            color = 0xFFC0C0C0,
            reflectionCoefficient = 0.98
        ),
        "BASALT" to MaterialProperties(
            name = "Basalt",
            conductivity = 1e-4,
            relativePermittivity = 8.0,
            relativePermeability = 1.05,
            density = 3.0,
            color = 0xFF4A4A4A,
            reflectionCoefficient = 0.3
        ),
        "LIMESTONE" to MaterialProperties(
            name = "Kalkstein",
            conductivity = 1e-5,
            relativePermittivity = 6.0,
            relativePermeability = 1.0,
            density = 2.7,
            color = 0xFFE6E6E6,
            reflectionCoefficient = 0.2
        ),
        "CLAY" to MaterialProperties(
            name = "Ton",
            conductivity = 1e-2,
            relativePermittivity = 25.0,
            relativePermeability = 1.0,
            density = 2.0,
            color = 0xFF8B4513,
            reflectionCoefficient = 0.4
        ),
        "WOOD" to MaterialProperties(
            name = "Holz",
            conductivity = 1e-10,
            relativePermittivity = 4.0,
            relativePermeability = 1.0,
            density = 0.7,
            color = 0xFF8B4513,
            reflectionCoefficient = 0.1
        ),
        "WATER" to MaterialProperties(
            name = "Wasser",
            conductivity = 0.5,
            relativePermittivity = 80.0,
            relativePermeability = 1.0,
            density = 1.0,
            color = 0xFF4169E1,
            reflectionCoefficient = 0.5
        ),
        "DIAMOND" to MaterialProperties(
            name = "Diamant",
            conductivity = 0.0,
            relativePermittivity = 6.1,
            relativePermeability = 0.9999, // Diamagnetisch
            density = 3.5,
            color = 0xFFB9F2FF,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.DIAMOND
        ),
        "RUBY" to MaterialProperties(
            name = "Rubin",
            conductivity = 0.0,
            relativePermittivity = 9.4,
            relativePermeability = 0.9999,
            density = 4.0,
            color = 0xFFE0115F,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.RUBY
        ),
        "EMERALD" to MaterialProperties(
            name = "Smaragd",
            conductivity = 0.0,
            relativePermittivity = 7.25,
            relativePermeability = 0.9999,
            density = 2.85,
            color = 0xFF50C878,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.EMERALD
        ),
        "SAPPHIRE" to MaterialProperties(
            name = "Saphir",
            conductivity = 0.0,
            relativePermittivity = 9.4,
            relativePermeability = 0.9999,
            density = 4.0,
            color = 0xFF0F52BA,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.SAPPHIRE
        ),
        "TOPAZ" to MaterialProperties(
            name = "Topas",
            conductivity = 0.0,
            relativePermittivity = 9.0,
            relativePermeability = 0.9999,
            density = 3.5,
            color = 0xFFFFC87C,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.TOPAZ
        ),
        "AMETHYST" to MaterialProperties(
            name = "Amethyst",
            conductivity = 0.0,
            relativePermittivity = 4.5,
            relativePermeability = 0.9999,
            density = 2.6,
            color = 0xFF9966CC,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.AMETHYST
        ),
        "TOURMALINE" to MaterialProperties(
            name = "Turmalin",
            conductivity = 0.0,
            relativePermittivity = 13.0,
            relativePermeability = 0.9999,
            density = 3.1,
            color = 0xFF00FF7F,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.TOURMALINE
        ),
        "GARNET" to MaterialProperties(
            name = "Granat",
            conductivity = 0.0,
            relativePermittivity = 10.0,
            relativePermeability = 0.9999,
            density = 4.0,
            color = 0xFF733635,
            reflectionCoefficient = 0.2,
            isGemstone = true,
            gemstoneType = GemstoneType.GARNET
        ),
        "PYRITE" to MaterialProperties(
            name = "Pyrit",
            conductivity = 1e4,
            relativePermittivity = 5.0,
            relativePermeability = 1.0,
            density = 5.0,
            color = 0xFFB8860B,
            reflectionCoefficient = 0.4
        ),
        "MAGNETITE" to MaterialProperties(
            name = "Magnetit",
            conductivity = 1e3,
            relativePermittivity = 4.0,
            relativePermeability = 100.0,
            density = 5.2,
            color = 0xFF2F4F4F,
            reflectionCoefficient = 0.3
        ),
        "QUARTZ" to MaterialProperties(
            name = "Quarz",
            conductivity = 0.0,
            relativePermittivity = 4.5,
            relativePermeability = 0.9999,
            density = 2.65,
            color = 0xFFE6E6FA,
            reflectionCoefficient = 0.1
        )
    )
    
    fun findMaterialByProperties(
        conductivity: Double,
        permittivity: Double,
        permeability: Double
    ): MaterialProperties? {
        return materials.values.minByOrNull { material ->
            val conductivityDiff = Math.abs(Math.log10(material.conductivity + 1e-20) - Math.log10(conductivity + 1e-20))
            val permittivityDiff = Math.abs(material.relativePermittivity - permittivity)
            val permeabilityDiff = Math.abs(material.relativePermeability - permeability)
            
            conductivityDiff + permittivityDiff + permeabilityDiff
        }
    }
    
    fun calculateSkinDepth(frequency: Double, material: MaterialProperties): Double {
        val omega = 2 * Math.PI * frequency
        val mu = 4 * Math.PI * 1e-7 * material.relativePermeability
        return Math.sqrt(2.0 / (omega * mu * material.conductivity))
    }
    
    fun calculateImpedance(frequency: Double, material: MaterialProperties): Complex {
        val omega = 2 * Math.PI * frequency
        val mu = 4 * Math.PI * 1e-7 * material.relativePermeability
        val epsilon = 8.854e-12 * material.relativePermittivity
        
        val sigma = material.conductivity
        val j = Complex(0.0, 1.0)
        
        return Complex.sqrt(
            Complex(j.real * omega * mu, j.imaginary * omega * mu) /
            Complex(sigma, omega * epsilon)
        )
    }
    
    fun calculateReflectionCoefficient(
        material1: MaterialProperties,
        material2: MaterialProperties,
        frequency: Double
    ): Complex {
        val z1 = calculateImpedance(frequency, material1)
        val z2 = calculateImpedance(frequency, material2)
        
        return (z2 - z1) / (z2 + z1)
    }
    
    fun detectGemstone(
        conductivity: Double,
        permittivity: Double,
        permeability: Double,
        magneticGradient: Double,
        anomalyShape: AnomalyShape
    ): GemstoneDetection? {
        // Prüfe auf Edelstein-Charakteristika
        if (conductivity < 1e-9 && 
            permittivity > 6.0 && 
            Math.abs(magneticGradient) < 0.1 &&
            (anomalyShape == AnomalyShape.CREVICE || 
             anomalyShape == AnomalyShape.POINT || 
             anomalyShape == AnomalyShape.VEIN)) {
            
            // Finde den passendsten Edelstein
            val gemstone = materials.values
                .filter { it.isGemstone }
                .minByOrNull { material ->
                    val permittivityDiff = Math.abs(material.relativePermittivity - permittivity)
                    val permeabilityDiff = Math.abs(material.relativePermeability - permeability)
                    permittivityDiff + permeabilityDiff
                }
            
            return gemstone?.let {
                GemstoneDetection(
                    gemstoneType = it.gemstoneType!!,
                    confidence = calculateGemstoneConfidence(
                        conductivity,
                        permittivity,
                        permeability,
                        magneticGradient,
                        anomalyShape,
                        it
                    ),
                    surroundingMinerals = detectSurroundingMinerals(
                        conductivity,
                        permittivity,
                        permeability
                    )
                )
            }
        }
        
        return null
    }
    
    private fun calculateGemstoneConfidence(
        conductivity: Double,
        permittivity: Double,
        permeability: Double,
        magneticGradient: Double,
        anomalyShape: AnomalyShape,
        gemstone: MaterialProperties
    ): Double {
        var confidence = 0.0
        
        // Leitfähigkeit (sollte sehr niedrig sein)
        confidence += if (conductivity < 1e-9) 0.3 else 0.0
        
        // Permittivität (sollte hoch sein)
        confidence += if (permittivity > 6.0) 0.2 else 0.0
        
        // Permeabilität (sollte nahe 1 sein)
        confidence += if (Math.abs(permeability - 1.0) < 0.1) 0.2 else 0.0
        
        // Magnetischer Gradient (sollte niedrig sein)
        confidence += if (Math.abs(magneticGradient) < 0.1) 0.1 else 0.0
        
        // Anomalieform (sollte passend sein)
        confidence += when (anomalyShape) {
            AnomalyShape.CREVICE, AnomalyShape.POINT, AnomalyShape.VEIN -> 0.2
            else -> 0.0
        }
        
        return confidence
    }
    
    private fun detectSurroundingMinerals(
        conductivity: Double,
        permittivity: Double,
        permeability: Double
    ): List<MaterialProperties> {
        return materials.values
            .filter { !it.isGemstone }
            .filter { material ->
                val conductivityMatch = Math.abs(Math.log10(material.conductivity + 1e-20) - Math.log10(conductivity + 1e-20)) < 2.0
                val permittivityMatch = Math.abs(material.relativePermittivity - permittivity) < 5.0
                val permeabilityMatch = Math.abs(material.relativePermeability - permeability) < 10.0
                
                conductivityMatch && (permittivityMatch || permeabilityMatch)
            }
    }
}

data class Complex(
    val real: Double,
    val imaginary: Double
) {
    operator fun plus(other: Complex) = Complex(
        real + other.real,
        imaginary + other.imaginary
    )
    
    operator fun minus(other: Complex) = Complex(
        real - other.real,
        imaginary - other.imaginary
    )
    
    operator fun times(other: Complex) = Complex(
        real * other.real - imaginary * other.imaginary,
        real * other.imaginary + imaginary * other.real
    )
    
    operator fun div(other: Complex): Complex {
        val denominator = other.real * other.real + other.imaginary * other.imaginary
        return Complex(
            (real * other.real + imaginary * other.imaginary) / denominator,
            (imaginary * other.real - real * other.imaginary) / denominator
        )
    }
    
    companion object {
        fun sqrt(z: Complex): Complex {
            val r = Math.sqrt(z.real * z.real + z.imaginary * z.imaginary)
            val theta = Math.atan2(z.imaginary, z.real)
            val sqrtR = Math.sqrt(r)
            val halfTheta = theta / 2
            
            return Complex(
                sqrtR * Math.cos(halfTheta),
                sqrtR * Math.sin(halfTheta)
            )
        }
    }
}

data class GemstoneDetection(
    val gemstoneType: GemstoneType,
    val confidence: Double,
    val surroundingMinerals: List<MaterialProperties>
)

enum class AnomalyShape {
    CREVICE,    // Spalt
    POINT,      // Punkt
    VEIN,       // Ader
    CAVITY,     // Hohlraum
    LAYER,      // Schicht
    UNKNOWN     // Unbekannt
} 