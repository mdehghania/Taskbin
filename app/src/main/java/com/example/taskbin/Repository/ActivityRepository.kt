package com.example.taskbin.Repository

import androidx.lifecycle.LiveData
import com.example.taskbin.Dao.ActivityDao
import com.example.taskbin.Model.ActivityEntity

class ActivityRepository(private val activityDao: ActivityDao) {

    fun getActivitiesByUserOwnerId(userOwnerId: Int): LiveData<List<ActivityEntity>> {
        return activityDao.getActivitiesByUserOwnerId(userOwnerId)
    }

    suspend fun insert(activity: ActivityEntity) {
        activityDao.insert(activity)
    }

    suspend fun update(activity: ActivityEntity) {
        activityDao.update(activity)
    }

    suspend fun delete(activity: ActivityEntity) {
        activityDao.delete(activity)
    }
    suspend fun updateCompletion(activityId: Int, completed: Boolean) {
        activityDao.updateCompletion(activityId, completed)
    }
}
