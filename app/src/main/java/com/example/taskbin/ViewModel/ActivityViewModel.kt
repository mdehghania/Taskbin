package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.Repository.ActivityRepository
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    fun getActivitiesByUserOwnerId(userOwnerId: Int): LiveData<List<ActivityEntity>> {
        return repository.getActivitiesByUserOwnerId(userOwnerId)
    }

    fun insert(activity: ActivityEntity) = viewModelScope.launch {
        repository.insert(activity)
    }

    fun update(activity: ActivityEntity) = viewModelScope.launch {
        repository.update(activity)
    }

    fun delete(activity: ActivityEntity) = viewModelScope.launch {
        repository.delete(activity)
    }

    fun updateCompletion(activityId: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.updateCompletion(
                activityId,
                completed
            )
        }
    }

}
