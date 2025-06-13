package com.emfad.app.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialAnalysis
import com.emfad.app.models.MaterialType
import com.emfad.app.models.MaterialClassifier

@Composable
fun MaterialAnalysisCard(
    analysis: MaterialAnalysis?,
    classifier: MaterialClassifier = MaterialClassifier(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Materialanalyse",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            analysis?.let {
                val (backgroundColor, textColor) = when (it.type) {
                    MaterialType.FERROUS_METAL -> Color(0xFFFFA500) to Color.White
                    MaterialType.NON_FERROUS_METAL -> Color(0xFF4CAF50) to Color.White
                    MaterialType.CAVITY -> Color(0xFF2196F3) to Color.White
                    MaterialType.UNKNOWN -> Color.Gray to Color.White
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (it.type) {
                                MaterialType.FERROUS_METAL -> "Eisenhaltiges Metall"
                                MaterialType.NON_FERROUS_METAL -> "Nicht-eisenhaltiges Metall"
                                MaterialType.CAVITY -> "Hohlraum"
                                MaterialType.UNKNOWN -> "Unbekanntes Material"
                            },
                            color = textColor,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = classifier.getMaterialDescription(it.type),
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Zuverlässigkeit: ${(it.confidence * 100).toInt()}%",
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } ?: Text(
                text = "Keine Analyse verfügbar",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 