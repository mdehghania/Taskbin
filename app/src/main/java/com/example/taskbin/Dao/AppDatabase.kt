package com.example.taskbin.Dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskbin.Model.ActivityEntity
import com.example.taskbin.Model.ProjectEntity
import com.example.taskbin.Model.StagesEntity
import com.example.taskbin.Model.TargetEntity
import com.example.taskbin.Model.UserEntity

@Database(entities = [UserEntity::class, ProjectEntity::class, TargetEntity::class, ActivityEntity::class, StagesEntity::class], version = 9)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun projectDao(): ProjectDao
    abstract fun targetDao(): TargetDao
    abstract fun activityDao(): ActivityDao
    abstract fun stagesDao(): StagesDao

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

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `project_stages` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `stageName` TEXT NOT NULL,
                        `isCompleted` INTEGER NOT NULL DEFAULT 0,
                        `projectId` INTEGER NOT NULL,
                        FOREIGN KEY(`projectId`) REFERENCES `project_table`(`projectId`) ON DELETE CASCADE
                    )
                """)
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS `project_stages`")
            }
        }

        // Migration 7 to 8: Remove columns from ProjectEntity and add new column, create new stage_table
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new project_table with the new structure
                database.execSQL("""
            CREATE TABLE project_table_new (
                projectId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                pName TEXT NOT NULL,
                pHour TEXT NOT NULL DEFAULT '',
                pTime TEXT NOT NULL,
                pDate TEXT NOT NULL,
                pPin INTEGER NOT NULL,
                userOwnerId INTEGER NOT NULL,
                FOREIGN KEY(userOwnerId) REFERENCES user_table(userId) ON DELETE CASCADE
            )
        """)

                // Copy the data from the old project_table to the new project_table
                database.execSQL("""
            INSERT INTO project_table_new (projectId, pName, pTime, pDate, pPin, userOwnerId)
            SELECT projectId, pName, pTime, pDate, pPin, userOwnerId
            FROM project_table
        """)

                // Drop the old project_table
                database.execSQL("DROP TABLE project_table")

                // Rename the new table to the original table name
                database.execSQL("ALTER TABLE project_table_new RENAME TO project_table")

                // Create the stage_table
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS stage_table (
                stageId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sName TEXT NOT NULL,
                sCheck INTEGER NOT NULL DEFAULT 0,
                projectOwnerId INTEGER NOT NULL,
                FOREIGN KEY(projectOwnerId) REFERENCES project_table(projectId) ON DELETE CASCADE
            )
        """)
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `project_table` ADD COLUMN `completed` INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9
                ).build()
                INSTANCE = instance
                instance
            }
        }

    }
}
