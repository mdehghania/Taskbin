package com.example.taskbin.Dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.taskbin.Model.ActivityEntity

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: ActivityEntity)

    @Update
    suspend fun update(activity: ActivityEntity)

    @Delete
    suspend fun delete(activity: ActivityEntity)

    @Query("SELECT * FROM activity_table WHERE userOwnerId = :userId")
    suspend fun getActivitiesByUser(userId: Int): List<ActivityEntity>
}