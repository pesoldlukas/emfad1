package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emfad.app.models.EMFADDevice
import com.emfad.app.viewmodels.BluetoothViewModel

@Composable
fun BluetoothPage(
    viewModel: BluetoothViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val discoveredDevices by viewModel.discoveredDevices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Status-Anzeige
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Bluetooth-Status: ${
                        when (uiState) {
                            is BluetoothViewModel.BluetoothUiState.Connected -> "Verbunden"
                            is BluetoothViewModel.BluetoothUiState.Connecting -> "Verbinde..."
                            is BluetoothViewModel.BluetoothUiState.Disconnected -> "Getrennt"
                            is BluetoothViewModel.BluetoothUiState.Error -> "Fehler: ${(uiState as BluetoothViewModel.BluetoothUiState.Error).message}"
                            else -> "Initial"
                        }
                    }",
                    style = MaterialTheme.typography.h6
                )
            }
        }

        // Scan-Button
        Button(
            onClick = { 
                if (isScanning) viewModel.stopDeviceScan() else viewModel.startDeviceScan()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(if (isScanning) "Scan stoppen" else "Nach Geräten suchen")
        }

        // Geräteliste
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(discoveredDevices) { device ->
                DeviceItem(
                    device = device,
                    onConnectClick = { viewModel.connectToDevice(device) }
                )
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: EMFADDevice,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.body2
                )
            }
            Button(
                onClick = onConnectClick,
                enabled = !device.isConnected
            ) {
                Text(if (device.isConnected) "Verbunden" else "Verbinden")
            }
        }
    }
} 