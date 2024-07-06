package com.example.taskbin.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskbin.Model.StagesEntity

@Dao
interface StagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stage: StagesEntity)

    @Query("SELECT * FROM stage_table WHERE projectOwnerId = :projectOwnerId")
    suspend fun getStagesForProject(projectOwnerId: Int): List<StagesEntity>
}
