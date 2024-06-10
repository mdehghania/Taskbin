package com.example.taskbin.Model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity (
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val username: String,
    val userPhone: String,
    val userPassword: String
)