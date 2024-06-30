package com.example.taskbin.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.Model.UserEntity

@Database(entities = [UserEntity::class, ProjectEntity::class, TargetEntity::class, ActivityEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun targetDao(): TargetDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `activity_table` (
                        `activityId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `aName` TEXT NOT NULL,
                        `aDescription` TEXT NOT NULL,
                        `aCategory` TEXT NOT NULL,
                        `aTime` TEXT NOT NULL,
                        `aHour` TEXT NOT NULL,
                        `aPin` INTEGER NOT NULL,
                        `userOwnerId` INTEGER NOT NULL,
                        FOREIGN KEY(`userOwnerId`) REFERENCES `user_table`(`userId`) ON DELETE CASCADE
                    )
                """)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
