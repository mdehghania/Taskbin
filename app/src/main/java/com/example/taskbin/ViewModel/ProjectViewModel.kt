package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.ProjectEntity
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

    fun getProjectsByUser(userId: Int, callback: (List<ProjectEntity>) -> Unit) = viewModelScope.launch {
        val projects = repository.getProjectsByUser(userId)
        callback(projects)
    }
}