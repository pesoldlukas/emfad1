package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialAnalysis
import com.emfad.app.models.MaterialType

@Composable
fun MaterialAnalysisCard(analysis: MaterialAnalysis) {
    val (color, description) = when (analysis.type) {
        MaterialType.METAL -> Color(0xFFFFD54F) to "Metal Detected"
        MaterialType.CAVITY -> Color(0xFF81C784) to "Cavity Detected"
        else -> MaterialTheme.colorScheme.surface to "Unknown Material"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Confidence: ${(analysis.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge
            )
            
            analysis.trend?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Trend: ${it.direction} (${(it.strength * 100).toInt()}%)",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
