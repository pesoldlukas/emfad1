package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.*
import java.util.Locale

@Composable
fun MaterialCalibrationView(
    calibration: MaterialCalibration,
    onAddCalibrationPoint: (CalibrationData) -> Unit,
    onRemoveCalibrationPoint: (CalibrationData) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMaterialType by remember { mutableStateOf<MaterialType?>(null) }
    var depth by remember { mutableStateOf("") }
    var magneticField by remember { mutableStateOf("") }
    var electricField by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var abValue by remember { mutableStateOf("") }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Materialkalibrierung",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Materialtyp-Auswahl
            MaterialTypeSelector(
                selectedType = selectedMaterialType,
                onTypeSelected = { selectedMaterialType = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Eingabefelder
            OutlinedTextField(
                value = depth,
                onValueChange = { depth = it },
                label = { Text("Tiefe (mm)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = magneticField,
                onValueChange = { magneticField = it },
                label = { Text("Magnetfeld (µT)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = electricField,
                onValueChange = { electricField = it },
                label = { Text("Elektrisches Feld (V/m)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = frequency,
                onValueChange = { frequency = it },
                label = { Text("Frequenz (Hz)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = abValue,
                onValueChange = { abValue = it },
                label = { Text("A/B-Wert") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kalibrierungspunkt hinzufügen
            Button(
                onClick = {
                    selectedMaterialType?.let { type ->
                        val calibrationData = CalibrationData(
                            materialType = type,
                            depth = depth.toFloatOrNull() ?: 0f,
                            magneticField = magneticField.toFloatOrNull() ?: 0f,
                            electricField = electricField.toFloatOrNull() ?: 0f,
                            frequency = frequency.toFloatOrNull() ?: 0f,
                            abValue = abValue.toFloatOrNull() ?: 0f
                        )
                        onAddCalibrationPoint(calibrationData)
                        
                        // Felder zurücksetzen
                        depth = ""
                        magneticField = ""
                        electricField = ""
                        frequency = ""
                        abValue = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMaterialType != null
            ) {
                Text("Kalibrierungspunkt hinzufügen")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Kalibrierungspunkte anzeigen
            Text(
                text = "Kalibrierungspunkte",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(MaterialType.values()) { materialType ->
                    CalibrationPointList(
                        materialType = materialType,
                        calibration = calibration,
                        onRemovePoint = onRemoveCalibrationPoint
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialTypeSelector(
    selectedType: MaterialType?,
    onTypeSelected: (MaterialType) -> Unit
) {
    Column {
        Text(
            text = "Materialtyp auswählen",
            style = MaterialTheme.typography.titleMedium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MaterialType.values().forEach { type ->
                FilterChip(
                    selected = type == selectedType,
                    onClick = { onTypeSelected(type) },
                    label = {
                        Text(
                            when (type) {
                                MaterialType.FERROUS_METAL -> "Eisenhaltig"
                                MaterialType.NON_FERROUS_METAL -> "Nicht-eisenhaltig"
                                MaterialType.CAVITY -> "Hohlraum"
                                else -> "Unbekannt"
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CalibrationPointList(
    materialType: MaterialType,
    calibration: MaterialCalibration,
    onRemovePoint: (CalibrationData) -> Unit
) {
    val quality = calibration.getCalibrationQuality(materialType)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (materialType) {
                        MaterialType.FERROUS_METAL -> "Eisenhaltiges Metall"
                        MaterialType.NON_FERROUS_METAL -> "Nicht-eisenhaltiges Metall"
                        MaterialType.CAVITY -> "Hohlraum"
                        else -> "Unbekanntes Material"
                    },
                    style = MaterialTheme.typography.titleSmall
                )
                
                Text(
                    text = "Qualität: ${(quality * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Kalibrierungsfaktoren anzeigen
            calibration.getCalibrationFactors(materialType)?.let { factors ->
                Text(
                    text = "Kalibrierungsfaktoren:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Magnetisch: ${String.format(Locale.GERMAN, "%.2f", factors.magneticFactor)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Elektrisch: ${String.format(Locale.GERMAN, "%.2f", factors.electricFactor)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Tiefe: ${String.format(Locale.GERMAN, "%.2f", factors.depthFactor)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Frequenz: ${String.format(Locale.GERMAN, "%.2f", factors.frequencyFactor)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
} 