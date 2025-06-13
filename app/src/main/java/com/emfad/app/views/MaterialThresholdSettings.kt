package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.MaterialClassifier

@Composable
fun MaterialThresholdSettings(
    classifier: MaterialClassifier,
    onThresholdsUpdated: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thresholds = classifier.getCurrentThresholds()
    var ferrousMagnetic by remember { mutableStateOf(thresholds["Ferrous Magnetic"] ?: 80f) }
    var nonFerrousElectric by remember { mutableStateOf(thresholds["Non-Ferrous Electric"] ?: 30f) }
    var nonFerrousMagnetic by remember { mutableStateOf(thresholds["Non-Ferrous Magnetic"] ?: 20f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Materialklassifizierung - Schwellenwerte",
                style = MaterialTheme.typography.h6
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ferrous Magnetic Threshold
            Column {
                Text("Ferromagnetisch (µT): ${ferrousMagnetic.toInt()}")
                Slider(
                    value = ferrousMagnetic,
                    onValueChange = { ferrousMagnetic = it },
                    valueRange = 20f..200f,
                    steps = 18
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Non-Ferrous Electric Threshold
            Column {
                Text("Nicht-Ferromagnetisch E-Feld (V/m): ${nonFerrousElectric.toInt()}")
                Slider(
                    value = nonFerrousElectric,
                    onValueChange = { nonFerrousElectric = it },
                    valueRange = 10f..100f,
                    steps = 9
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Non-Ferrous Magnetic Threshold
            Column {
                Text("Nicht-Ferromagnetisch M-Feld (µT): ${nonFerrousMagnetic.toInt()}")
                Slider(
                    value = nonFerrousMagnetic,
                    onValueChange = { nonFerrousMagnetic = it },
                    valueRange = 5f..50f,
                    steps = 9
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    classifier.updateThresholds(
                        ferrousMagnetic = ferrousMagnetic,
                        nonFerrousElectric = nonFerrousElectric,
                        nonFerrousMagnetic = nonFerrousMagnetic
                    )
                    onThresholdsUpdated()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Schwellenwerte speichern")
            }
        }
    }
} 