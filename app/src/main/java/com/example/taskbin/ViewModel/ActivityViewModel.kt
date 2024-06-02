package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.Repository.ActivityRepository
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    fun insert(activity: ActivityEntity) = viewModelScope.launch {
        repository.insert(activity)
    }

    fun update(activity: ActivityEntity) = viewModelScope.launch {
        repository.update(activity)
    }

    fun delete(activity: ActivityEntity) = viewModelScope.launch {
        repository.delete(activity)
    }

    fun getActivitiesByUser(userId: Int, callback: (List<ActivityEntity>) -> Unit) = viewModelScope.launch {
        val activities = repository.getActivitiesByUser(userId)
        callback(activities)
    }
}