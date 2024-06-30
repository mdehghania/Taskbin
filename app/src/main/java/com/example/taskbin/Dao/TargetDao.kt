package com.example.taskbin.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.taskbin.Model.TargetEntity

@Dao
interface TargetDao {
    @Insert
    suspend fun insert(target: TargetEntity)

    @Update
    suspend fun update(target: TargetEntity)

    @Delete
    suspend fun delete(target: TargetEntity)

    @Query("SELECT * FROM target_table WHERE userOwnerId = :userId")
    suspend fun getTargetsByUser(userId: Int): List<TargetEntity>

    @Query("SELECT * FROM target_table WHERE userOwnerId = :userOwnerId")
    fun getTargetsByUserOwnerId(userOwnerId: Int): LiveData<List<TargetEntity>>

    @Query("SELECT * FROM target_table")
    fun getAllTargets(): LiveData<List<TargetEntity>>


}
