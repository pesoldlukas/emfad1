package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialType

@Composable
fun MaterialCalibrationView(
    onCalibrationComplete: (MaterialType, Double) -> Unit
) {
    var selectedMaterial by remember { mutableStateOf(MaterialType.METAL) }
    var calibrationValue by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Material Calibration", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Material Type Selection
        Text("Select Material Type", style = MaterialTheme.typography.titleMedium)
        Row {
            MaterialType.values().forEach { type ->
                FilterChip(
                    selected = selectedMaterial == type,
                    onClick = { selectedMaterial = type },
                    label = { Text(type.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Calibration Value Input
        OutlinedTextField(
            value = calibrationValue,
            onValueChange = { calibrationValue = it },
            label = { Text("Calibration Value") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Calibration Button
        Button(
            onClick = {
                val value = calibrationValue.toDoubleOrNull() ?: 0.0
                onCalibrationComplete(selectedMaterial, value)
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Calibrate")
        }
    }
}
