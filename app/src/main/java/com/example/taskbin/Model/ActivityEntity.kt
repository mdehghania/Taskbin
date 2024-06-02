package com.example.taskbin.Model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_table",
    foreignKeys = [ForeignKey(
        entity = UserEntity::class,
        parentColumns = ["userId"],
        childColumns = ["userOwnerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val activityId: Int = 0,
    val aName: String,
    val aCategory: String,
    val aTime: String,
    val aHour: String,
    val aPin: Boolean,
    val userOwnerId: Int
)