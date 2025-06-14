package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.AnalysisThresholds

@Composable
fun MaterialThresholdSettings(
    thresholds: AnalysisThresholds,
    onThresholdsChanged: (AnalysisThresholds) -> Unit
) {
    var metalThreshold by remember { mutableStateOf(thresholds.magneticFieldMetalThreshold.toString()) }
    var cavityThreshold by remember { mutableStateOf(thresholds.electricFieldCavityThreshold.toString()) }
    var confidenceThreshold by remember { mutableStateOf(thresholds.confidenceThreshold.toString()) }
    var trendWindow by remember { mutableStateOf(thresholds.trendWindowSize.toString()) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Material Threshold Settings", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Metal Detection Threshold
        OutlinedTextField(
            value = metalThreshold,
            onValueChange = { metalThreshold = it },
            label = { Text("Metal Detection Threshold (µT)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cavity Detection Threshold
        OutlinedTextField(
            value = cavityThreshold,
            onValueChange = { cavityThreshold = it },
            label = { Text("Cavity Detection Threshold (V/m)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Confidence Threshold
        OutlinedTextField(
            value = confidenceThreshold,
            onValueChange = { confidenceThreshold = it },
            label = { Text("Confidence Threshold (0.0-1.0)") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Trend Window Size
        OutlinedTextField(
            value = trendWindow,
            onValueChange = { trendWindow = it },
            label = { Text("Trend Window Size") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Save Button
        Button(
            onClick = {
                onThresholdsChanged(
                    thresholds.copy(
                        magneticFieldMetalThreshold = metalThreshold.toFloatOrNull() ?: thresholds.magneticFieldMetalThreshold,
                        electricFieldCavityThreshold = cavityThreshold.toFloatOrNull() ?: thresholds.electricFieldCavityThreshold,
                        confidenceThreshold = confidenceThreshold.toFloatOrNull() ?: thresholds.confidenceThreshold,
                        trendWindowSize = trendWindow.toIntOrNull() ?: thresholds.trendWindowSize
                    )
                )
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Save Settings")
        }
    }
}
