package com.example.taskbin.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "target_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class TargetEntity(
    @PrimaryKey(autoGenerate = true) val targetId: Int = 0,
    val tName: String,
    val tDesc: String,
    val userOwnerId: Int,
    val completed: Boolean
)