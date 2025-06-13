package com.emfad.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emfad.app.viewmodels.SettingsViewModel
import com.emfad.app.viewmodels.SettingsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Einstellungen") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Zurück")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Messparameter
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Messparameter",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Frequenz
                    OutlinedTextField(
                        value = uiState.frequency.toString(),
                        onValueChange = { viewModel.setFrequency(it.toDoubleOrNull() ?: 1000.0) },
                        label = { Text("Frequenz (Hz)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Antennenabstand
                    OutlinedTextField(
                        value = uiState.antennaDistance.toString(),
                        onValueChange = { viewModel.setAntennaDistance(it.toDoubleOrNull() ?: 1.0) },
                        label = { Text("Antennenabstand (m)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Messgenauigkeit
                    Column {
                        Text(
                            "Messgenauigkeit",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Slider(
                            value = uiState.measurementAccuracy,
                            onValueChange = viewModel::setMeasurementAccuracy,
                            valueRange = 0.1f..1.0f,
                            steps = 9,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text(
                            "${(uiState.measurementAccuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Kalibrierung
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Kalibrierung",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Automatische Kalibrierung
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Automatische Kalibrierung")
                        Switch(
                            checked = uiState.autoCalibration,
                            onCheckedChange = viewModel::setAutoCalibration
                        )
                    }
                    
                    // Kalibrierungsgenauigkeit
                    Column {
                        Text(
                            "Kalibrierungsgenauigkeit",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        
                        Slider(
                            value = uiState.calibrationAccuracy,
                            onValueChange = viewModel::setCalibrationAccuracy,
                            valueRange = 0.1f..1.0f,
                            steps = 9,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Text(
                            "${(uiState.calibrationAccuracy * 100).toInt()}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            
            // Materialerkennung
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Materialerkennung",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    // Metallerkennung
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Metallerkennung")
                        Switch(
                            checked = uiState.metalDetection,
                            onCheckedChange = viewModel::setMetalDetection
                        )
                    }
                    
                    // Kristallerfassung
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kristallerfassung")
                        Switch(
                            checked = uiState.crystalDetection,
                            onCheckedChange = viewModel::setCrystalDetection
                        )
                    }
                    
                    // Clusteranalyse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Clusteranalyse")
                        Switch(
                            checked = uiState.clusterAnalysis,
                            onCheckedChange = viewModel::setClusterAnalysis
                        )
                    }
                    
                    // Einschlussanalyse
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Einschlussanalyse")
                        Switch(
                            checked = uiState.inclusionAnalysis,
                            onCheckedChange = viewModel::setInclusionAnalysis
                        )
                    }
                }
            }
            
            // Speichern-Button
            Button(
                onClick = {
                    viewModel.saveSettings()
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Einstellungen speichern")
            }
        }
    }
} 