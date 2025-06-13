package com.emfad.app.Views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.Models.*
import com.emfad.app.ViewModels.MainViewModel
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.rememberLineChartState

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val settings by viewModel.currentSettings.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status Bar
        StatusBar(uiState)

        // Measurement Display
        MeasurementDisplay(viewModel)

        // Control Buttons
        ControlButtons(
            uiState = uiState,
            onStartMeasurement = { viewModel.startMeasurement() },
            onStopMeasurement = { viewModel.stopMeasurement() },
            onSettingsClick = { showSettings = true }
        )

        // Settings Dialog
        if (showSettings) {
            SettingsDialog(
                settings = settings,
                onSettingsChanged = { viewModel.updateSettings(it) },
                onDismiss = { showSettings = false }
            )
        }
    }
}

@Composable
private fun StatusBar(uiState: MainViewModel.UiState) {
    val (backgroundColor, text) = when (uiState) {
        is MainViewModel.UiState.Connected -> MaterialTheme.colorScheme.primary to "Verbunden"
        is MainViewModel.UiState.Measuring -> MaterialTheme.colorScheme.tertiary to "Messung läuft"
        is MainViewModel.UiState.Disconnected -> MaterialTheme.colorScheme.error to "Nicht verbunden"
        is MainViewModel.UiState.Error -> MaterialTheme.colorScheme.error to "Fehler: ${uiState.message}"
        MainViewModel.UiState.Initial -> MaterialTheme.colorScheme.surface to "Bereit"
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun MeasurementDisplay(viewModel: MainViewModel) {
    // Placeholder for measurement visualization
    // This will be replaced with actual measurement data visualization
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Messwerte werden hier angezeigt")
    }
}

@Composable
private fun ControlButtons(
    uiState: MainViewModel.UiState,
    onStartMeasurement: () -> Unit,
    onStopMeasurement: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onSettingsClick,
            enabled = uiState !is MainViewModel.UiState.Measuring
        ) {
            Text("Einstellungen")
        }

        when (uiState) {
            is MainViewModel.UiState.Connected -> {
                Button(onClick = onStartMeasurement) {
                    Text("Messung starten")
                }
            }
            is MainViewModel.UiState.Measuring -> {
                Button(
                    onClick = onStopMeasurement,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Messung beenden")
                }
            }
            else -> {
                // No action buttons for other states
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    settings: MeasurementSettings,
    onSettingsChanged: (MeasurementSettings) -> Unit,
    onDismiss: () -> Unit
) {
    var frequency by remember { mutableStateOf(settings.frequency.toString()) }
    var mode by remember { mutableStateOf(settings.mode) }
    var orientation by remember { mutableStateOf(settings.orientation) }
    var filterLevel by remember { mutableStateOf(settings.filterLevel.toString()) }
    var gain by remember { mutableStateOf(settings.gain.toString()) }
    var offset by remember { mutableStateOf(settings.offset.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Einstellungen") },
        text = {
            Column {
                OutlinedTextField(
                    value = frequency,
                    onValueChange = { frequency = it },
                    label = { Text("Frequenz (kHz)") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Messmodus")
                Row {
                    MeasurementMode.values().forEach { measurementMode ->
                        RadioButton(
                            selected = mode == measurementMode,
                            onClick = { mode = measurementMode }
                        )
                        Text(measurementMode.name)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Orientierung")
                Row {
                    MeasurementOrientation.values().forEach { measurementOrientation ->
                        RadioButton(
                            selected = orientation == measurementOrientation,
                            onClick = { orientation = measurementOrientation }
                        )
                        Text(measurementOrientation.name)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = filterLevel,
                    onValueChange = { filterLevel = it },
                    label = { Text("Filterstufe") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = gain,
                    onValueChange = { gain = it },
                    label = { Text("Verstärkung") }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = offset,
                    onValueChange = { offset = it },
                    label = { Text("Offset") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSettingsChanged(
                        settings.copy(
                            frequency = frequency.toDoubleOrNull() ?: settings.frequency,
                            mode = mode,
                            orientation = orientation,
                            filterLevel = filterLevel.toIntOrNull() ?: settings.filterLevel,
                            gain = gain.toDoubleOrNull() ?: settings.gain,
                            offset = offset.toDoubleOrNull() ?: settings.offset
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Abbrechen")
            }
        }
    )
} 