package com.example.taskbin.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.taskbin.Model.UserEntity

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM user_table WHERE username = :username AND userPassword = :password LIMIT 1")
    suspend fun getUser(username: String, password: String): UserEntity?
}