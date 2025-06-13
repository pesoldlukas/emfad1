package com.emfad.app.models

import kotlin.math.sqrt
import kotlin.math.pow

data class MeasurementPoint(
    val impedance: Complex,
    val conductivity: Double,
    val permittivity: Complex,
    val permeability: Double,
    val depth: Double,
    val position: Point3D
)

data class Point3D(val x: Double, val y: Double, val z: Double) {
    fun distanceTo(other: Point3D): Double {
        return sqrt(
            (x - other.x).pow(2) +
            (y - other.y).pow(2) +
            (z - other.z).pow(2)
        )
    }
}

data class ClusterAnalysisResult(
    val clusters: List<Cluster>,
    val outliers: List<MeasurementPoint>,
    val confidence: Double
)

data class Cluster(
    val points: List<MeasurementPoint>,
    val centroid: Point3D,
    val radius: Double,
    val type: ClusterType
)

enum class ClusterType {
    NATURAL_VEIN,
    ARTIFICIAL_STRUCTURE,
    CRYSTAL_FORMATION,
    UNKNOWN
}

class ClusterAnalyzer {
    companion object {
        private const val OUTLIER_THRESHOLD = 2.0 // Standardabweichungen
        private const val MIN_CLUSTER_SIZE = 3
        private const val MAX_CLUSTER_RADIUS = 2.0 // Meter
    }
    
    fun analyzeClusters(points: List<MeasurementPoint>): ClusterAnalysisResult {
        // 1. Berechne Distanzmatrix
        val distanceMatrix = calculateDistanceMatrix(points)
        
        // 2. Identifiziere Outlier
        val (clusters, outliers) = identifyClustersAndOutliers(points, distanceMatrix)
        
        // 3. Bestimme Clustertypen
        val typedClusters = clusters.map { cluster ->
            cluster.copy(type = determineClusterType(cluster))
        }
        
        // 4. Berechne Gesamtzuverlässigkeit
        val confidence = calculateConfidence(typedClusters, outliers)
        
        return ClusterAnalysisResult(
            clusters = typedClusters,
            outliers = outliers,
            confidence = confidence
        )
    }
    
    private fun calculateDistanceMatrix(points: List<MeasurementPoint>): Array<DoubleArray> {
        val n = points.size
        val matrix = Array(n) { DoubleArray(n) }
        
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                val distance = calculateFeatureDistance(points[i], points[j])
                matrix[i][j] = distance
                matrix[j][i] = distance
            }
        }
        
        return matrix
    }
    
    private fun calculateFeatureDistance(a: MeasurementPoint, b: MeasurementPoint): Double {
        // Gewichtete euklidische Distanz der Features
        val weights = mapOf(
            "impedance" to 0.3,
            "conductivity" to 0.2,
            "permittivity" to 0.2,
            "permeability" to 0.1,
            "depth" to 0.2
        )
        
        val impedanceDist = (a.impedance - b.impedance).magnitude
        val conductivityDist = (a.conductivity - b.conductivity).pow(2)
        val permittivityDist = (a.permittivity - b.permittivity).magnitude
        val permeabilityDist = (a.permeability - b.permeability).pow(2)
        val depthDist = (a.depth - b.depth).pow(2)
        val spatialDist = a.position.distanceTo(b.position)
        
        return sqrt(
            weights["impedance"]!! * impedanceDist.pow(2) +
            weights["conductivity"]!! * conductivityDist +
            weights["permittivity"]!! * permittivityDist.pow(2) +
            weights["permeability"]!! * permeabilityDist +
            weights["depth"]!! * depthDist +
            spatialDist.pow(2)
        )
    }
    
    private fun identifyClustersAndOutliers(
        points: List<MeasurementPoint>,
        distanceMatrix: Array<DoubleArray>
    ): Pair<List<Cluster>, List<MeasurementPoint>> {
        val n = points.size
        val visited = BooleanArray(n)
        val clusters = mutableListOf<Cluster>()
        val outliers = mutableListOf<MeasurementPoint>()
        
        // Berechne Durchschnittsdistanz und Standardabweichung
        val distances = distanceMatrix.flatMap { it.toList() }.filter { it > 0 }
        val meanDistance = distances.average()
        val stdDev = sqrt(distances.map { (it - meanDistance).pow(2) }.average())
        
        // DBSCAN-ähnlicher Algorithmus
        for (i in 0 until n) {
            if (visited[i]) continue
            
            val neighbors = mutableListOf<Int>()
            for (j in 0 until n) {
                if (distanceMatrix[i][j] < meanDistance + OUTLIER_THRESHOLD * stdDev) {
                    neighbors.add(j)
                }
            }
            
            if (neighbors.size >= MIN_CLUSTER_SIZE) {
                // Erstelle Cluster
                val clusterPoints = neighbors.map { points[it] }
                val centroid = calculateCentroid(clusterPoints)
                val radius = calculateClusterRadius(clusterPoints, centroid)
                
                clusters.add(Cluster(
                    points = clusterPoints,
                    centroid = centroid,
                    radius = radius,
                    type = ClusterType.UNKNOWN
                ))
                
                neighbors.forEach { visited[it] = true }
            } else {
                outliers.add(points[i])
                visited[i] = true
            }
        }
        
        return Pair(clusters, outliers)
    }
    
    private fun calculateCentroid(points: List<MeasurementPoint>): Point3D {
        val sumX = points.sumOf { it.position.x }
        val sumY = points.sumOf { it.position.y }
        val sumZ = points.sumOf { it.position.z }
        val n = points.size.toDouble()
        
        return Point3D(sumX / n, sumY / n, sumZ / n)
    }
    
    private fun calculateClusterRadius(
        points: List<MeasurementPoint>,
        centroid: Point3D
    ): Double {
        return points.maxOf { it.position.distanceTo(centroid) }
    }
    
    private fun determineClusterType(cluster: Cluster): ClusterType {
        val points = cluster.points
        
        // Berechne Durchschnittswerte
        val avgConductivity = points.map { it.conductivity }.average()
        val avgDepth = points.map { it.depth }.average()
        val aspectRatio = calculateAspectRatio(points)
        val symmetry = calculateSymmetry(points)
        
        return when {
            // Natürliche Ader
            avgConductivity > 1e4 && aspectRatio > 10 && symmetry < 0.3 && avgDepth > 1.5 ->
                ClusterType.NATURAL_VEIN
            
            // Künstliche Struktur
            (avgConductivity < 1e-9 || avgConductivity > 1e6) && 
            aspectRatio < 3 && symmetry > 0.7 && avgDepth < 10 ->
                ClusterType.ARTIFICIAL_STRUCTURE
            
            // Kristallformation
            avgConductivity < 1e-10 && symmetry > 0.8 ->
                ClusterType.CRYSTAL_FORMATION
            
            else -> ClusterType.UNKNOWN
        }
    }
    
    private fun calculateAspectRatio(points: List<MeasurementPoint>): Double {
        val xRange = points.map { it.position.x }.let { it.maxOrNull()!! - it.minOrNull()!! }
        val yRange = points.map { it.position.y }.let { it.maxOrNull()!! - it.minOrNull()!! }
        val zRange = points.map { it.position.z }.let { it.maxOrNull()!! - it.minOrNull()!! }
        
        val maxRange = maxOf(xRange, yRange, zRange)
        val minRange = minOf(xRange, yRange, zRange)
        
        return if (minRange > 0) maxRange / minRange else 1.0
    }
    
    private fun calculateSymmetry(points: List<MeasurementPoint>): Double {
        val centroid = calculateCentroid(points)
        val distances = points.map { it.position.distanceTo(centroid) }
        val meanDistance = distances.average()
        val stdDev = sqrt(distances.map { (it - meanDistance).pow(2) }.average())
        
        return 1.0 - (stdDev / meanDistance).coerceIn(0.0, 1.0)
    }
    
    private fun calculateConfidence(
        clusters: List<Cluster>,
        outliers: List<MeasurementPoint>
    ): Double {
        var confidence = 0.0
        
        // 1. Clustergröße (30%)
        val avgClusterSize = clusters.map { it.points.size }.average()
        confidence += 0.3 * (avgClusterSize / 10.0).coerceIn(0.0, 1.0)
        
        // 2. Cluster-Trennung (30%)
        val clusterSeparation = calculateClusterSeparation(clusters)
        confidence += 0.3 * clusterSeparation
        
        // 3. Outlier-Anteil (20%)
        val outlierRatio = outliers.size.toDouble() / (clusters.sumOf { it.points.size } + outliers.size)
        confidence += 0.2 * (1.0 - outlierRatio)
        
        // 4. Clustertyp-Übereinstimmung (20%)
        val typeConfidence = clusters.count { it.type != ClusterType.UNKNOWN }.toDouble() / clusters.size
        confidence += 0.2 * typeConfidence
        
        return confidence.coerceIn(0.0, 1.0)
    }
    
    private fun calculateClusterSeparation(clusters: List<Cluster>): Double {
        if (clusters.size < 2) return 1.0
        
        var minSeparation = Double.MAX_VALUE
        for (i in clusters.indices) {
            for (j in i + 1 until clusters.size) {
                val separation = clusters[i].centroid.distanceTo(clusters[j].centroid) /
                               (clusters[i].radius + clusters[j].radius)
                minSeparation = minOf(minSeparation, separation)
            }
        }
        
        return (minSeparation / 2.0).coerceIn(0.0, 1.0)
    }
} 