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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emfad.app.models.*
import com.emfad.app.viewmodels.MainViewModel
import com.emfad.app.viewmodels.MainUiState
import com.emfad.app.viewmodels.MaterialAnalysisResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EMFAD Scanner") },
                actions = {
                    IconButton(onClick = { /* TODO: Einstellungen öffnen */ }) {
                        Icon(Icons.Default.Settings, "Einstellungen")
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
            // Messmodus-Auswahl
            MeasurementModeSelector(
                currentMode = uiState.currentMode,
                onModeSelected = viewModel::setMeasurementMode
            )
            
            // Messung und Kalibrierung
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = viewModel::startMeasurement,
                    enabled = !uiState.isMeasuring && !uiState.isCalibrating,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isMeasuring) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Messen")
                    }
                }
                
                Button(
                    onClick = viewModel::startCalibration,
                    enabled = !uiState.isMeasuring && !uiState.isCalibrating,
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isCalibrating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Kalibrieren")
                    }
                }
            }
            
            // Letzte Messung
            uiState.lastMeasurement?.let { measurement ->
                MeasurementResultCard(measurement)
            }
            
            // Letzte Kalibrierung
            uiState.lastCalibration?.let { calibration ->
                CalibrationResultCard(calibration)
            }
            
            // Materialanalyse
            uiState.materialAnalysis?.let { analysis ->
                MaterialAnalysisCard(analysis)
            }
            
            // Fehlermeldung
            uiState.error?.let { error ->
                ErrorCard(error) {
                    viewModel.clearError()
                }
            }
        }
    }
}

@Composable
fun MeasurementModeSelector(
    currentMode: MeasurementMode,
    onModeSelected: (MeasurementMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Messmodus",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MeasurementMode.values().forEach { mode ->
                    FilterChip(
                        selected = mode == currentMode,
                        onClick = { onModeSelected(mode) },
                        label = {
                            Text(
                                when (mode) {
                                    MeasurementMode.BA_VERTICAL -> "B-A Vertikal"
                                    MeasurementMode.AB_HORIZONTAL -> "A-B Horizontal"
                                    MeasurementMode.ANTENNA_A -> "Antenne A"
                                    MeasurementMode.DEPTH_PRO -> "Tiefenprofil"
                                }
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MeasurementResultCard(measurement: MeasurementResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Letzte Messung",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Frequenz:")
                Text("${measurement.frequency} Hz")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Impedanz:")
                Text("${measurement.impedance.real} + ${measurement.impedance.imaginary}i Ω")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tiefe:")
                Text("${measurement.depth} m")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Zuverlässigkeit:")
                Text("${(measurement.confidence * 100).toInt()}%")
            }
        }
    }
}

@Composable
fun CalibrationResultCard(calibration: CalibrationResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Kalibrierungsergebnis",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Status:")
                Text(
                    if (calibration.success) "Erfolgreich" else "Fehlgeschlagen",
                    color = if (calibration.success)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Kalibrierungsfaktor:")
                Text(calibration.calibrationFactor.toString())
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Zuverlässigkeit:")
                Text("${(calibration.confidence * 100).toInt()}%")
            }
        }
    }
}

@Composable
fun MaterialAnalysisCard(analysis: MaterialAnalysisResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Materialanalyse",
                style = MaterialTheme.typography.titleMedium
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Material:")
                Text(analysis.materialName)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Typ:")
                Text(
                    when (analysis.materialType) {
                        MaterialType.NATURAL_VEIN -> "Natürliche Ader"
                        MaterialType.ARTIFICIAL_STRUCTURE -> "Künstliche Struktur"
                        MaterialType.CRYSTAL -> "Kristall"
                        MaterialType.UNKNOWN -> "Unbekannt"
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Tiefe:")
                Text("${analysis.depth} m")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Größe:")
                Text("${analysis.size} m")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Zuverlässigkeit:")
                Text("${(analysis.confidence * 100).toInt()}%")
            }
            
            // Materialeigenschaften
            Text(
                "Materialeigenschaften",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Leitfähigkeit:")
                Text("${analysis.properties.conductivity} S/m")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Permittivität:")
                Text("${analysis.properties.permittivity.real} + ${analysis.properties.permittivity.imaginary}i")
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Permeabilität:")
                Text(analysis.properties.permeability.toString())
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Dichte:")
                Text("${analysis.properties.density} g/cm³")
            }
        }
    }
}

@Composable
fun ErrorCard(
    error: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                error,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    "Schließen",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
} 