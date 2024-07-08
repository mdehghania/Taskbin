package com.example.taskbin.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.taskbin.Dao.ProjectDao
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity


class ProjectRepository(private val projectDao: ProjectDao) {

    suspend fun insert(project: ProjectEntity) {
        projectDao.insert(project)
    }

    suspend fun update(project: ProjectEntity) {
        projectDao.update(project)
    }

    suspend fun delete(project: ProjectEntity) {
        projectDao.delete(project)
    }

    //    suspend fun getProjectsByUser(userId: Int): List<ProjectEntity> {
//        return projectDao.getProjectsByUser(userId)
//    }
    fun getProjectsByUser(userId: Int): LiveData<List<ProjectEntity>> {
        Log.d("ProjectRepository", "Fetching projects for userId: $userId")
        return projectDao.getProjectsByUserOwnerId(userId)
    }

    suspend fun insertProjectWithStages(project: ProjectEntity, stages: List<StagesEntity>) {
        projectDao.insertProjectWithStages(project, stages)
    }

    suspend fun getProjectWithStages(projectId: Int) = projectDao.getProjectWithStages(projectId)


    suspend fun updateCompletion(projectId: Int, completed: Boolean) {
        projectDao.updateCompletion(projectId, completed)
    }
}