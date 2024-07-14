package com.example.taskbin.Repository

import androidx.lifecycle.LiveData
import com.example.taskbin.Dao.TargetDao
import com.example.taskbin.Model.TargetEntity

class TargetRepository(private val targetDao: TargetDao) {

    suspend fun insert(target: TargetEntity) {

        targetDao.insert(target)
    }

    suspend fun update(target: TargetEntity) {
        targetDao.update(target)
    }

    suspend fun delete(target: TargetEntity) {
        targetDao.delete(target)
    }

    fun getTargetsByUser(userId: Int): LiveData<List<TargetEntity>> {
        return targetDao.getTargetsByUserOwnerId(userId)
    }

    suspend fun updateCompletion(targetId: Int, completed: Boolean) {
        targetDao.updateCompletion(targetId, completed)
    }
}
