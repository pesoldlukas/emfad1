package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.models.*

@Composable
fun MainView(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    var isCalibrating by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tabs für verschiedene Ansichten
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Messung") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Kalibrierung") }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Messmodus-Auswahl
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MeasurementMode.values().forEach { mode ->
                FilterChip(
                    selected = viewModel.currentMode.value == mode,
                    onClick = { viewModel.setMeasurementMode(mode) },
                    label = {
                        Text(
                            when (mode) {
                                MeasurementMode.B_A_VERTICAL -> "B-A Vertikal"
                                MeasurementMode.A_B_HORIZONTAL -> "A-B Horizontal"
                                MeasurementMode.ANTENNA_A -> "Antenne A"
                                MeasurementMode.DEPTH_PRO -> "Tiefe Pro"
                            }
                        )
                    }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Inhalt basierend auf ausgewähltem Tab
        when (selectedTab) {
            0 -> {
                // Messungsansicht
                Column {
                    // Messwerte
                    MeasurementCard(
                        measurement = viewModel.currentMeasurement,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Materialanalyse
                    viewModel.currentAnalysis?.let { analysis ->
                        MaterialPhysicsAnalysisCard(
                            analysis = analysis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 3D-Visualisierung
                        Material3DVisualization(
                            analysis = analysis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            1 -> {
                // Kalibrierungsansicht
                Column {
                    // Automatische Kalibrierung
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Automatische Kalibrierung",
                                style = MaterialTheme.typography.h6
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = viewModel.getCalibrationStatus(),
                                style = MaterialTheme.typography.body1
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = {
                                        if (isCalibrating) {
                                            viewModel.finishCalibration()
                                            isCalibrating = false
                                        } else {
                                            viewModel.startCalibration()
                                            isCalibrating = true
                                        }
                                    }
                                ) {
                                    Text(if (isCalibrating) "Kalibrierung beenden" else "Kalibrierung starten")
                                }
                            }
                        }
                    }
                    
                    // Manuelle Kalibrierung
                    MaterialCalibrationView(
                        calibration = viewModel.calibration,
                        onAddCalibrationPoint = { calibrationData ->
                            viewModel.addCalibrationPoint(calibrationData)
                        },
                        onRemoveCalibrationPoint = { calibrationData ->
                            viewModel.removeCalibrationPoint(calibrationData)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
} 