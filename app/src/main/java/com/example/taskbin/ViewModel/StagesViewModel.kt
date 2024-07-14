package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.StagesEntity
import com.example.taskbin.Repository.StagesRepository
import kotlinx.coroutines.launch

class StagesViewModel(private val repository: StagesRepository) : ViewModel() {
    fun getStagesByProjectId(projectId: Int): LiveData<List<StagesEntity>> {
        return repository.getStagesByProjectId(projectId)
    }

    fun updateStage(stage: StagesEntity) {
        viewModelScope.launch {
            repository.updateStage(stage)
        }
    }
}