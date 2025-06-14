package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialPhysicsAnalysis

@Composable
fun MaterialPhysicsAnalysisCard(analysis: MaterialPhysicsAnalysis) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Physics Analysis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = MaterialTheme.typography.titleLarge.fontWeight
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Depth Information
            AnalysisRow("Depth", "${analysis.depth} mm", "${(analysis.depthConfidence * 100).toInt()}%")
            
            // Size Information
            AnalysisRow("Size", "${analysis.size} mm", "")
            
            // Conductivity
            AnalysisRow("Conductivity", "${analysis.conductivity} S/m", "")
            
            // Skin Depth
            AnalysisRow("Skin Depth", "${analysis.skinDepth} mm", "")
            
            // Confidence
            AnalysisRow("Confidence", "", "${(analysis.confidence * 100).toInt()}%")
        }
    }
}

@Composable
private fun AnalysisRow(title: String, value: String, confidence: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Row {
            if (value.isNotEmpty()) {
                Text(value, style = MaterialTheme.typography.bodyMedium)
            }
            if (confidence.isNotEmpty()) {
                Spacer(modifier = Modifier.width(16.dp))
                Text("($confidence)", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
