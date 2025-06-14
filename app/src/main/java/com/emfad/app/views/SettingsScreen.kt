package com.emfad.app.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.emfad.app.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController
) {
    val thresholds by viewModel.materialThresholds.collectAsState()
    val crystalDB by viewModel.crystalDatabase.collectAsState()
    val veinDB by viewModel.veinDatabase.collectAsState()
    val structureDB by viewModel.structureDatabase.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
        ) {
            // Threshold Settings
            Text("Detection Thresholds", style = MaterialTheme.typography.titleLarge)
            MaterialThresholdSettings(
                thresholds = thresholds,
                onThresholdsChanged = viewModel::updateThresholds
            )
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            // Material Database
            Text("Material Database", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            
            Text("Crystals: ${crystalDB.size} entries")
            Text("Natural Veins: ${veinDB.size} entries")
            Text("Artificial Structures: ${structureDB.size} entries")
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.resetToDefaultDatabase() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Reset to Default Database")
            }
        }
    }
}
