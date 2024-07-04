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
    var aName: String,
    var aDescription: String,
    var aCategory: String,
    var aTime: String,
    var aHour: String,
    var aPin: Boolean,
    var aDate: Long ,
    var userOwnerId: Int,
    var completed: Boolean
)
