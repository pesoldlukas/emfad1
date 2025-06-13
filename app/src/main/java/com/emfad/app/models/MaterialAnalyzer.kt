package com.emfad.app.models

data class MaterialAnalysis(
    val type: MaterialType,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val trend: MaterialTrend? = null
)

enum class MaterialType {
    METAL,
    CAVITY,
    UNKNOWN
}

data class MaterialTrend(
    val direction: TrendDirection,
    val strength: Float,
    val lastChange: Long
)

enum class TrendDirection {
    INCREASING,
    DECREASING,
    STABLE
}

class MaterialAnalyzer(private val thresholds: AnalysisThresholds = AnalysisThresholds()) {
    private val recentAnalyses = mutableListOf<MaterialAnalysis>()

    fun analyzeMeasurement(measurement: EMFADMeasurement): MaterialAnalysis {
        val magneticField = measurement.magneticField
        val electricField = measurement.electricField
        
        val metalConfidence = calculateMetalConfidence(magneticField)
        val cavityConfidence = calculateCavityConfidence(electricField)
        
        val analysis = when {
            metalConfidence > thresholds.confidenceThreshold -> 
                MaterialAnalysis(MaterialType.METAL, metalConfidence)
            cavityConfidence > thresholds.confidenceThreshold -> 
                MaterialAnalysis(MaterialType.CAVITY, cavityConfidence)
            else -> 
                MaterialAnalysis(MaterialType.UNKNOWN, 0f)
        }

        // Trendanalyse
        recentAnalyses.add(analysis)
        if (recentAnalyses.size > thresholds.trendWindowSize) {
            recentAnalyses.removeAt(0)
        }
        
        val trend = analyzeTrend()
        return analysis.copy(trend = trend)
    }

    private fun calculateMetalConfidence(magneticField: Float): Float {
        return (magneticField / thresholds.magneticFieldMetalThreshold)
            .coerceIn(0f, 1f)
    }

    private fun calculateCavityConfidence(electricField: Float): Float {
        return (electricField / thresholds.electricFieldCavityThreshold)
            .coerceIn(0f, 1f)
    }

    private fun analyzeTrend(): MaterialTrend? {
        if (recentAnalyses.size < 2) return null

        val typeChanges = recentAnalyses.zipWithNext { a, b -> a.type != b.type }
        val lastChangeIndex = typeChanges.indexOfLast { it }
        
        if (lastChangeIndex == -1) {
            return MaterialTrend(
                direction = TrendDirection.STABLE,
                strength = 1f,
                lastChange = recentAnalyses.first().timestamp
            )
        }

        val confidenceChanges = recentAnalyses.zipWithNext { a, b -> 
            b.confidence - a.confidence 
        }
        
        val averageChange = confidenceChanges.average().toFloat()
        
        return MaterialTrend(
            direction = when {
                averageChange > 0.1f -> TrendDirection.INCREASING
                averageChange < -0.1f -> TrendDirection.DECREASING
                else -> TrendDirection.STABLE
            },
            strength = averageChange.absoluteValue.coerceIn(0f, 1f),
            lastChange = recentAnalyses[lastChangeIndex].timestamp
        )
    }

    fun getAnalysisHistory(): List<MaterialAnalysis> = recentAnalyses.toList()

    fun clearHistory() {
        recentAnalyses.clear()
    }
} 