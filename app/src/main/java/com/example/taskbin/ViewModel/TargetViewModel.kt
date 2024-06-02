package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.Repository.TargetRepository
import kotlinx.coroutines.launch

class TargetViewModel(private val repository: TargetRepository) : ViewModel() {

    fun insert(target: TargetEntity) = viewModelScope.launch {
        repository.insert(target)
    }

    fun update(target: TargetEntity) = viewModelScope.launch {
        repository.update(target)
    }

    fun delete(target: TargetEntity) = viewModelScope.launch {
        repository.delete(target)
    }

    fun getTargetsByUser(userId: Int, callback: (List<TargetEntity>) -> Unit) = viewModelScope.launch {
        val targets = repository.getTargetsByUser(userId)
        callback(targets)
    }
}