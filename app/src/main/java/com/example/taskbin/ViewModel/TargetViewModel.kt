package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
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

    fun getTargetsByUserOwnerId(userOwnerId: Int): LiveData<List<TargetEntity>> {
        return repository.getTargetsByUser(userOwnerId)
    }
    fun updateCompletion(targetId: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.updateCompletion(
                targetId,
                completed
            )
        }
    }
}
