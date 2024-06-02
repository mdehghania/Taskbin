package com.example.taskbin.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val projectId: Int = 0,
    val pName: String,
    val pStages: String,
    val pTime: String,
    val pDate: String,
    val pReminder: Boolean,
    val pPin: Boolean,
    val userOwnerId: Int
)