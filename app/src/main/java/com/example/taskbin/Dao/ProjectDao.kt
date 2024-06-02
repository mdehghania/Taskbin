package com.example.taskbin.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.taskbin.Model.ProjectEntity

@Dao
interface ProjectDao {
    @Insert
    suspend fun insert(project: ProjectEntity)

    @Update
    suspend fun update(project: ProjectEntity)

    @Delete
    suspend fun delete(project: ProjectEntity)

    @Query("SELECT * FROM project_table WHERE userOwnerId = :userId")
    suspend fun getProjectsByUser(userId: Int): List<ProjectEntity>
}