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

@Database(entities = [UserEntity::class, ProjectEntity::class, TargetEntity::class, ActivityEntity::class], version = 5)
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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `target_table` ADD COLUMN `completed` INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `activity_table` ADD COLUMN `completed` INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `target_table` ADD COLUMN `timestamp` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
