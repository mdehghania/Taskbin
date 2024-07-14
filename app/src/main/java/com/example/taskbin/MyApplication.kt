package com.example.taskbin

import android.app.Application
import com.example.taskbin.Dao.AppDatabase
import com.example.taskbin.Repository.ActivityRepository
import com.example.taskbin.Repository.ProjectRepository
import com.example.taskbin.Repository.StagesRepository
import com.example.taskbin.Repository.TargetRepository
import com.example.taskbin.Repository.UserRepository

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val projectRepository by lazy { ProjectRepository(database.projectDao()) }
    val targetRepository by lazy { TargetRepository(database.targetDao()) }
    val activityRepository by lazy { ActivityRepository(database.activityDao()) }
    val stagesRepository by lazy { StagesRepository(database.stagesDao()) }
}