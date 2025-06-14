package com.emfad.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.emfad.app.models.AnalysisThresholds
import com.emfad.app.models.MaterialDatabase
import com.emfad.app.models.MaterialProperties
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val _materialThresholds = MutableStateFlow(AnalysisThresholds())
    val materialThresholds: StateFlow<AnalysisThresholds> = _materialThresholds
    
    private val _crystalDatabase = MutableStateFlow<List<MaterialProperties>>(emptyList())
    val crystalDatabase: StateFlow<List<MaterialProperties>> = _crystalDatabase
    
    private val _veinDatabase = MutableStateFlow<List<MaterialProperties>>(emptyList())
    val veinDatabase: StateFlow<List<MaterialProperties>> = _veinDatabase
    
    private val _structureDatabase = MutableStateFlow<List<MaterialProperties>>(emptyList())
    val structureDatabase: StateFlow<List<MaterialProperties>> = _structureDatabase

    init {
        viewModelScope.launch {
            loadMaterialDatabases()
        }
    }

    private fun loadMaterialDatabases() {
        val db = MaterialDatabase()
        _crystalDatabase.value = db.getMaterialsByType(MaterialType.CRYSTAL)
        _veinDatabase.value = db.getMaterialsByType(MaterialType.NATURAL_VEIN)
        _structureDatabase.value = db.getMaterialsByType(MaterialType.ARTIFICIAL_STRUCTURE)
    }

    fun updateThresholds(newThresholds: AnalysisThresholds) {
        _materialThresholds.value = newThresholds
        // Persist to shared preferences or database
    }

    fun addCustomMaterial(material: MaterialProperties) {
        viewModelScope.launch {
            when (material.type) {
                MaterialType.CRYSTAL -> {
                    val updated = _crystalDatabase.value.toMutableList()
                    updated.add(material)
                    _crystalDatabase.value = updated
                }
                MaterialType.NATURAL_VEIN -> {
                    val updated = _veinDatabase.value.toMutableList()
                    updated.add(material)
                    _veinDatabase.value = updated
                }
                MaterialType.ARTIFICIAL_STRUCTURE -> {
                    val updated = _structureDatabase.value.toMutableList()
                    updated.add(material)
                    _structureDatabase.value = updated
                }
                else -> {}
            }
        }
    }

    fun removeCustomMaterial(material: MaterialProperties) {
        viewModelScope.launch {
            when (material.type) {
                MaterialType.CRYSTAL -> {
                    _crystalDatabase.value = _crystalDatabase.value.filter { it.name != material.name }
                }
                MaterialType.NATURAL_VEIN -> {
                    _veinDatabase.value = _veinDatabase.value.filter { it.name != material.name }
                }
                MaterialType.ARTIFICIAL_STRUCTURE -> {
                    _structureDatabase.value = _structureDatabase.value.filter { it.name != material.name }
                }
                else -> {}
            }
        }
    }

    fun resetToDefaultDatabase() {
        viewModelScope.launch {
            loadMaterialDatabases()
        }
    }
}
