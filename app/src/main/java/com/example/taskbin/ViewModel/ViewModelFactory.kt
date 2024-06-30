package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskbin.Repository.ActivityRepository
import com.example.taskbin.Repository.TargetRepository
import com.example.taskbin.Repository.UserRepository

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository? = null,
    private val targetRepository: TargetRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java) && activityRepository != null) {
            @Suppress("UNCHECKED_CAST")
            return ActivityViewModel(activityRepository) as T
        }
        if (modelClass.isAssignableFrom(TargetViewModel::class.java) && targetRepository != null) {
            @Suppress("UNCHECKED_CAST")
            return TargetViewModel(targetRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
