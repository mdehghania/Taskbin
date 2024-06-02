package com.example.taskbin.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.Model.UserEntity

@Database(entities = [UserEntity::class, ProjectEntity::class, TargetEntity::class, ActivityEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun targetDao(): TargetDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}