package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emfad.app.Services.BluetoothService
import com.emfad.app.ViewModels.MainViewModel
import com.emfad.app.bluetooth.EMFADDevice
import com.emfad.app.bluetooth.MeasurementMode

@Composable
fun BluetoothPage(viewModel: MainViewModel) {
    val bluetoothService = viewModel.bluetoothService
    val devices by bluetoothService.discoveredDevices.collectAsState()
    val connectionState by bluetoothService.connectionState.collectAsState()
    val receivedData by bluetoothService.receivedData.collectAsState()

    LaunchedEffect(Unit) {
        bluetoothService.startScan()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Connection Status
        ConnectionStatus(connectionState)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Device List
        DeviceList(devices, bluetoothService)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Received Data
        ReceivedDataDisplay(receivedData)
    }
}

@Composable
private fun ConnectionStatus(state: BluetoothService.ConnectionState) {
    val (text, color) = when (state) {
        is BluetoothService.ConnectionState.CONNECTED -> "Connected" to MaterialTheme.colorScheme.primary
        is BluetoothService.ConnectionState.CONNECTING -> "Connecting..." to MaterialTheme.colorScheme.secondary
        is BluetoothService.ConnectionState.SCANNING -> "Scanning for devices" to MaterialTheme.colorScheme.secondary
        is BluetoothService.ConnectionState.DISCONNECTED -> "Disconnected" to MaterialTheme.colorScheme.error
        is BluetoothService.ConnectionState.ERROR -> "Error: ${state.message}" to MaterialTheme.colorScheme.error
    }
    
    Surface(
        color = color,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun DeviceList(
    devices: List<EMFADDevice>,
    bluetoothService: BluetoothService
) {
    Text("Available Devices", style = MaterialTheme.typography.titleMedium)
    
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(devices) { device ->
            DeviceItem(device, bluetoothService)
        }
    }
}

@Composable
private fun DeviceItem(device: EMFADDevice, bluetoothService: BluetoothService) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(device.name, style = MaterialTheme.typography.bodyLarge)
                Text(device.address, style = MaterialTheme.typography.bodySmall)
            }
            
            Button(
                onClick = { bluetoothService.connectToDevice(device.bluetoothDevice) }
            ) {
                Text("Connect")
            }
        }
    }
}

@Composable
private fun ReceivedDataDisplay(data: String) {
    if (data.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Received Data", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(data)
            }
        }
    }
}
