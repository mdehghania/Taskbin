package com.example.taskbin.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskbin.Model.StagesEntity

@Dao
interface StagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stage: StagesEntity)

    @Query("SELECT * FROM stage_table WHERE projectOwnerId = :projectOwnerId")
    suspend fun getStagesForProject(projectOwnerId: Int): List<StagesEntity>

    @Query("SELECT * FROM stage_table WHERE projectOwnerId = :projectId")
    fun getStagesByProjectId(projectId: Int): LiveData<List<StagesEntity>>

    @Update
    suspend fun updateStage(stage: StagesEntity)

}
