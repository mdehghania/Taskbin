package com.example.taskbin.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity

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

    @Insert
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert
    suspend fun insertStages(stages: List<StagesEntity>)

    @Transaction
    suspend fun insertProjectWithStages(project: ProjectEntity, stages: List<StagesEntity>) {
        val projectId = insertProject(project)
        stages.forEach { it.projectOwnerId = projectId.toInt() }
        insertStages(stages)
    }

    @Query("SELECT * FROM project_table WHERE projectId = :projectId")
    suspend fun getProjectWithStages(projectId: Int): ProjectWithStages
}

data class ProjectWithStages(
    @Embedded val project: ProjectEntity,
    @Relation(
        parentColumn = "projectId",
        entityColumn = "projectOwnerId"
    )
    val stages: List<StagesEntity>
)

