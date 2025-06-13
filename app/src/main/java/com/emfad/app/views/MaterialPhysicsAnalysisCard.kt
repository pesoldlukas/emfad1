package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialPhysicsAnalysis
import com.emfad.app.models.MaterialType
import com.emfad.app.models.LayerAnalysis

@Composable
fun MaterialPhysicsAnalysisCard(
    analysis: MaterialPhysicsAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Physikalische Materialanalyse",
                style = MaterialTheme.typography.h6
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 3D-Visualisierung
            Material3DVisualization(
                analysis = analysis,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Materialtyp
            MaterialTypeRow(analysis.materialType)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tiefenanalyse
            AnalysisSection("Tiefenanalyse") {
                AnalysisRow(
                    label = "Geschätzte Tiefe",
                    value = String.format("%.1f mm", analysis.depth)
                )
                AnalysisRow(
                    label = "Tiefen-Zuverlässigkeit",
                    value = String.format("%.1f%%", analysis.depthConfidence * 100)
                )
                AnalysisRow(
                    label = "Skin-Tiefe",
                    value = String.format("%.1f mm", analysis.skinDepth)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grundlegende Messwerte
            AnalysisSection("Grundlegende Messwerte") {
                AnalysisRow(
                    label = "Geschätzte Größe",
                    value = String.format("%.1f mm", analysis.size)
                )
                AnalysisRow(
                    label = "Leitfähigkeit",
                    value = String.format("%.2e S/m", analysis.conductivity)
                )
                AnalysisRow(
                    label = "Magnetgradient",
                    value = String.format("%.1f nT/m", analysis.magneticGradient)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mengen- und Volumenanalyse
            AnalysisSection("Mengen- und Volumenanalyse") {
                analysis.massEstimate?.let { mass ->
                    AnalysisRow(
                        label = "Geschätzte Masse",
                        value = String.format("%.1f kg", mass)
                    )
                }
                analysis.volumeEstimate?.let { volume ->
                    AnalysisRow(
                        label = "Geschätztes Volumen",
                        value = String.format("%.3f m³", volume)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Schichtanalyse
            if (analysis.layerAnalysis.isNotEmpty()) {
                AnalysisSection("Schichtanalyse") {
                    analysis.layerAnalysis.forEach { layer ->
                        LayerAnalysisRow(layer)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Gesamtzuverlässigkeit
            AnalysisRow(
                label = "Gesamtzuverlässigkeit",
                value = String.format("%.1f%%", analysis.confidence * 100)
            )
        }
    }
}

@Composable
private fun AnalysisSection(
    title: String,
    content: @Composable () -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    content()
}

@Composable
private fun LayerAnalysisRow(layer: LayerAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "${layer.material} (${layer.depth} mm)",
                style = MaterialTheme.typography.subtitle2
            )
            AnalysisRow(
                label = "Leitfähigkeit",
                value = String.format("%.2e S/m", layer.conductivity)
            )
            AnalysisRow(
                label = "Magnetgradient",
                value = String.format("%.1f nT/m", layer.magneticGradient)
            )
            AnalysisRow(
                label = "Dielektrizitätskonstante",
                value = String.format("%.1f", layer.dielectricConstant)
            )
            AnalysisRow(
                label = "Dichte",
                value = String.format("%.1f g/cm³", layer.density)
            )
        }
    }
}

@Composable
private fun MaterialTypeRow(type: MaterialType) {
    val (backgroundColor, textColor) = when (type) {
        MaterialType.FERROUS_METAL -> MaterialTheme.colors.primary to MaterialTheme.colors.onPrimary
        MaterialType.NON_FERROUS_METAL -> MaterialTheme.colors.secondary to MaterialTheme.colors.onSecondary
        MaterialType.CAVITY -> MaterialTheme.colors.error to MaterialTheme.colors.onError
        MaterialType.UNKNOWN -> MaterialTheme.colors.surface to MaterialTheme.colors.onSurface
    }
    
    Surface(
        color = backgroundColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = when (type) {
                MaterialType.FERROUS_METAL -> "Eisenhaltiges Metall"
                MaterialType.NON_FERROUS_METAL -> "Nicht-eisenhaltiges Metall"
                MaterialType.CAVITY -> "Hohlraum"
                MaterialType.UNKNOWN -> "Unbekanntes Material"
            },
            color = textColor,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun AnalysisRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(
            text = value,
            style = MaterialTheme.typography.body1
        )
    }
} 