package com.example.taskbin.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "stage_table",
    foreignKeys = [ForeignKey(
        entity = ProjectEntity::class,
        parentColumns = ["projectId"],
        childColumns = ["projectOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)

class StagesEntity (
    @PrimaryKey(autoGenerate = true) val stageId: Int = 0,
    val sName: String,
    val sCheck :Boolean,
    val projectOwnerId: Int
)