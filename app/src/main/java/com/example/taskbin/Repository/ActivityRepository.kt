package com.example.taskbin.Repository

import com.example.taskbin.Dao.ActivityDao
import com.example.taskbin.Model.ActivityEntity


class ActivityRepository(private val activityDao: ActivityDao) {

    suspend fun insert(activity: ActivityEntity) {
        activityDao.insert(activity)
    }

    suspend fun update(activity: ActivityEntity) {
        activityDao.update(activity)
    }

    suspend fun delete(activity: ActivityEntity) {
        activityDao.delete(activity)
    }

    suspend fun getActivitiesByUser(userId: Int): List<ActivityEntity> {
        return activityDao.getActivitiesByUser(userId)
    }
}