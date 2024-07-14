package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskbin.Repository.ActivityRepository
import com.example.taskbin.Repository.ProjectRepository
import com.example.taskbin.Repository.StagesRepository
import com.example.taskbin.Repository.TargetRepository
import com.example.taskbin.Repository.UserRepository

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository? = null,
    private val targetRepository: TargetRepository? = null,
    private val projectRepository: ProjectRepository? = null,
    private val stagesRepository: StagesRepository? = null
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
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java) && projectRepository != null) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(projectRepository) as T
        }
        if (modelClass.isAssignableFrom(StagesViewModel::class.java) && stagesRepository != null) {
            @Suppress("UNCHECKED_CAST")
            return StagesViewModel(stagesRepository) as T
        }
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
