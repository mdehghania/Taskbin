package com.example.taskbin.Repository

import com.example.taskbin.Dao.ProjectDao
import com.example.taskbin.Model.ProjectEntity


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

    suspend fun getProjectsByUser(userId: Int): List<ProjectEntity> {
        return projectDao.getProjectsByUser(userId)
    }
}