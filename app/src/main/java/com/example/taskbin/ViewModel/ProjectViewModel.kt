package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity
import com.example.taskbin.Repository.ProjectRepository
import kotlinx.coroutines.launch

class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {

    fun insert(project: ProjectEntity) = viewModelScope.launch {
        repository.insert(project)
    }

    fun update(project: ProjectEntity) = viewModelScope.launch {
        repository.update(project)
    }

    fun delete(project: ProjectEntity) = viewModelScope.launch {
        repository.delete(project)
    }

    fun getProjectByUserOwnerId(userOwnerId: Int): LiveData<List<ProjectEntity>> {
        return repository.getProjectsByUser(userOwnerId)
    }


    fun insertProjectWithStages(project: ProjectEntity, stages: List<StagesEntity>) {
        viewModelScope.launch {
            repository.insertProjectWithStages(project, stages)
        }
    }

    fun updateCompletion(projectId: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.updateCompletion(projectId, completed)
        }
    }

    fun getStagesByProjectOwnerId(projectOwnerId: Int): LiveData<List<StagesEntity>> {
        val stagesLiveData = MutableLiveData<List<StagesEntity>>()
        viewModelScope.launch {
            val stages = repository.getStagesByProjectOwnerId(projectOwnerId)
            stagesLiveData.postValue(stages)
        }
        return stagesLiveData
    }
}
