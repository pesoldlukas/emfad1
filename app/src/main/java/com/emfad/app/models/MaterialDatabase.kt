package com.emfad.app.models

data class MaterialProperties(
    val name: String,
    val conductivity: Double,
    val permittivity: Complex,
    val permeability: Double,
    val density: Double,
    val type: MaterialType,
    val color: String,
    val typicalDepth: Double,
    val typicalSize: Double
)

enum class MaterialType {
    NATURAL_VEIN,
    ARTIFICIAL_STRUCTURE,
    CRYSTAL,
    UNKNOWN
}

class MaterialDatabase {
    companion object {
        private val MATERIALS = mapOf(
            // Natürliche Adern
            "gold_vein" to MaterialProperties(
                name = "Goldader",
                conductivity = 4.1e7,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 19.32,
                type = MaterialType.NATURAL_VEIN,
                color = "Gelb",
                typicalDepth = 2.5,
                typicalSize = 0.5
            ),
            "silver_vein" to MaterialProperties(
                name = "Silberader",
                conductivity = 6.3e7,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 10.49,
                type = MaterialType.NATURAL_VEIN,
                color = "Silber",
                typicalDepth = 2.0,
                typicalSize = 0.4
            ),
            "copper_vein" to MaterialProperties(
                name = "Kupferader",
                conductivity = 5.8e7,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 8.96,
                type = MaterialType.NATURAL_VEIN,
                color = "Rotbraun",
                typicalDepth = 1.8,
                typicalSize = 0.6
            ),
            "quartz_vein" to MaterialProperties(
                name = "Quarzader",
                conductivity = 1e-10,
                permittivity = Complex(4.5, 0.0),
                permeability = 1.0,
                density = 2.65,
                type = MaterialType.NATURAL_VEIN,
                color = "Weiß",
                typicalDepth = 1.5,
                typicalSize = 0.8
            ),
            "pyrite_vein" to MaterialProperties(
                name = "Pyritader",
                conductivity = 1e4,
                permittivity = Complex(5.0, 0.0),
                permeability = 1.0,
                density = 5.0,
                type = MaterialType.NATURAL_VEIN,
                color = "Gelb",
                typicalDepth = 1.2,
                typicalSize = 0.7
            ),
            
            // Künstliche Strukturen
            "tunnel" to MaterialProperties(
                name = "Tunnel",
                conductivity = 1e-12,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 0.0,
                type = MaterialType.ARTIFICIAL_STRUCTURE,
                color = "Schwarz",
                typicalDepth = 5.0,
                typicalSize = 2.0
            ),
            "chamber" to MaterialProperties(
                name = "Kammer",
                conductivity = 1e-12,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 0.0,
                type = MaterialType.ARTIFICIAL_STRUCTURE,
                color = "Schwarz",
                typicalDepth = 3.0,
                typicalSize = 3.0
            ),
            "well" to MaterialProperties(
                name = "Brunnen",
                conductivity = 1e-12,
                permittivity = Complex(1.0, 0.0),
                permeability = 1.0,
                density = 0.0,
                type = MaterialType.ARTIFICIAL_STRUCTURE,
                color = "Schwarz",
                typicalDepth = 4.0,
                typicalSize = 1.0
            ),
            "foundation" to MaterialProperties(
                name = "Fundament",
                conductivity = 1e-6,
                permittivity = Complex(8.0, 0.0),
                permeability = 1.0,
                density = 2.4,
                type = MaterialType.ARTIFICIAL_STRUCTURE,
                color = "Grau",
                typicalDepth = 1.0,
                typicalSize = 2.0
            ),
            "wall" to MaterialProperties(
                name = "Mauer",
                conductivity = 1e-6,
                permittivity = Complex(8.0, 0.0),
                permeability = 1.0,
                density = 2.4,
                type = MaterialType.ARTIFICIAL_STRUCTURE,
                color = "Grau",
                typicalDepth = 0.5,
                typicalSize = 0.3
            ),
            
            // Kristalle
            "ruby" to MaterialProperties(
                name = "Rubin",
                conductivity = 1e-10,
                permittivity = Complex(9.5, 0.0),
                permeability = 1.0,
                density = 4.0,
                type = MaterialType.CRYSTAL,
                color = "Rot",
                typicalDepth = 1.5,
                typicalSize = 0.1
            ),
            "emerald" to MaterialProperties(
                name = "Smaragd",
                conductivity = 1e-10,
                permittivity = Complex(7.0, 0.0),
                permeability = 1.0,
                density = 2.75,
                type = MaterialType.CRYSTAL,
                color = "Grün",
                typicalDepth = 1.2,
                typicalSize = 0.1
            ),
            "diamond" to MaterialProperties(
                name = "Diamant",
                conductivity = 1e-13,
                permittivity = Complex(6.0, 0.0),
                permeability = 1.0,
                density = 3.55,
                type = MaterialType.CRYSTAL,
                color = "Weiß",
                typicalDepth = 2.0,
                typicalSize = 0.05
            ),
            "tourmaline" to MaterialProperties(
                name = "Turmalin",
                conductivity = 1e-9,
                permittivity = Complex(13.5, 0.0),
                permeability = 1.0,
                density = 3.15,
                type = MaterialType.CRYSTAL,
                color = "Schwarz",
                typicalDepth = 1.0,
                typicalSize = 0.2
            )
        )
    }
    
    fun getMaterial(key: String): MaterialProperties? = MATERIALS[key]
    
    fun getAllMaterials(): List<MaterialProperties> = MATERIALS.values.toList()
    
    fun getMaterialsByType(type: MaterialType): List<MaterialProperties> =
        MATERIALS.values.filter { it.type == type }
    
    fun findMatchingMaterial(
        conductivity: Double,
        permittivity: Complex,
        permeability: Double,
        density: Double
    ): MaterialProperties? {
        return MATERIALS.values.minByOrNull { material ->
            calculateMaterialDistance(
                material,
                conductivity,
                permittivity,
                permeability,
                density
            )
        }
    }
    
    private fun calculateMaterialDistance(
        material: MaterialProperties,
        conductivity: Double,
        permittivity: Complex,
        permeability: Double,
        density: Double
    ): Double {
        val conductivityDist = abs(log10(material.conductivity) - log10(conductivity))
        val permittivityDist = abs(material.permittivity.magnitude - permittivity.magnitude)
        val permeabilityDist = abs(material.permeability - permeability)
        val densityDist = abs(material.density - density)
        
        return sqrt(
            conductivityDist.pow(2) +
            permittivityDist.pow(2) +
            permeabilityDist.pow(2) +
            densityDist.pow(2)
        )
    }
    
    private fun log10(x: Double): Double = kotlin.math.ln(x) / kotlin.math.ln(10.0)
} 