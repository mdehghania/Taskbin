package com.example.taskbin.Repository

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

    fun getProjectsByUser(userId: Int): LiveData<List<ProjectEntity>> {
        return projectDao.getProjectsByUserOwnerId(userId)
    }

    suspend fun insertProjectWithStages(project: ProjectEntity, stages: List<StagesEntity>) {
        projectDao.insertProjectWithStages(project, stages)
    }

    suspend fun getStagesByProjectOwnerId(projectOwnerId: Int): List<StagesEntity> {
        return projectDao.getStagesByProjectOwnerId(projectOwnerId)
    }

    suspend fun updateCompletion(projectId: Int, completed: Boolean) {
        projectDao.updateCompletion(projectId, completed)
    }
}