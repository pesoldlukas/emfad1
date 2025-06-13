package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emfad.app.models.MeasurementSession
import com.emfad.app.viewmodels.MeasurementViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

@Composable
fun MeasurementPage(
    viewModel: MeasurementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()
    val measurements by viewModel.measurements.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val materialAnalysis by viewModel.materialAnalysis.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sitzungsstatus
        SessionStatusCard(currentSession)

        Spacer(modifier = Modifier.height(16.dp))

        // Materialanalyse
        MaterialAnalysisCard(materialAnalysis)

        Spacer(modifier = Modifier.height(16.dp))

        // Aktuelle Messwerte
        CurrentMeasurementCard(measurements.lastOrNull())

        Spacer(modifier = Modifier.height(16.dp))

        // Diagramm
        MeasurementChart(measurements)

        Spacer(modifier = Modifier.height(16.dp))

        // Statistiken
        StatisticsCard(statistics)

        Spacer(modifier = Modifier.height(16.dp))

        // Aktionsbuttons
        ActionButtons(
            uiState = uiState,
            onStartSession = { viewModel.startNewSession("EMFAD_UG12DS") },
            onEndSession = { viewModel.endCurrentSession() },
            onExport = { viewModel.exportCurrentSession() }
        )
    }
}

@Composable
private fun SessionStatusCard(session: MeasurementSession?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Messsitzung: ${session?.let { "Aktiv" } ?: "Keine"}",
                style = MaterialTheme.typography.h6
            )
            session?.let {
                Text("Ort: ${it.location}")
                Text("Notizen: ${it.notes}")
                Text("Dauer: ${it.duration / 1000} Sekunden")
            }
        }
    }
}

@Composable
private fun CurrentMeasurementCard(measurement: EMFADMeasurement?) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Aktuelle Messung",
                style = MaterialTheme.typography.h6
            )
            measurement?.let {
                Text("E-Feld: ${it.electricField} V/m")
                Text("M-Feld: ${it.magneticField} µT")
                Text("Frequenz: ${it.frequency} Hz")
                Text("Batterie: ${it.batteryLevel}%")
            }
        }
    }
}

@Composable
private fun MeasurementChart(measurements: List<EMFADMeasurement>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Messverlauf",
                style = MaterialTheme.typography.h6
            )
            Chart(
                chart = lineChart(),
                model = entryModelOf(measurements.map { it.electricField.toFloat() }),
                startAxis = startAxis(),
                bottomAxis = bottomAxis(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
        }
    }
}

@Composable
private fun StatisticsCard(statistics: Map<String, Double>) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Statistiken",
                style = MaterialTheme.typography.h6
            )
            statistics.forEach { (key, value) ->
                Text("$key: $value")
            }
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: MeasurementViewModel.MeasurementUiState,
    onStartSession: () -> Unit,
    onEndSession: () -> Unit,
    onExport: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        when (uiState) {
            is MeasurementViewModel.MeasurementUiState.Initial,
            is MeasurementViewModel.MeasurementUiState.SessionEnded -> {
                Button(onClick = onStartSession) {
                    Text("Neue Messung starten")
                }
            }
            is MeasurementViewModel.MeasurementUiState.Measuring -> {
                Button(onClick = onEndSession) {
                    Text("Messung beenden")
                }
            }
            else -> {}
        }
        
        if (uiState is MeasurementViewModel.MeasurementUiState.SessionEnded) {
            Button(onClick = onExport) {
                Text("Exportieren")
            }
        }
    }
} 