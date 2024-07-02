package com.example.taskbin.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.taskbin.Dao.TargetDao
import com.example.taskbin.Model.TargetEntity

class TargetRepository(private val targetDao: TargetDao) {
    val allTargets: LiveData<List<TargetEntity>> = targetDao.getAllTargets()

    suspend fun insert(target: TargetEntity) {
        Log.d("TargetRepository", "Inserting target: $target")
        targetDao.insert(target)
    }

    suspend fun update(target: TargetEntity) {
        Log.d("TargetRepository", "Updating target: $target")
        targetDao.update(target)
    }

    suspend fun delete(target: TargetEntity) {
        Log.d("TargetRepository", "Deleting target: $target")
        targetDao.delete(target)
    }

    fun getTargetsByUser(userId: Int): LiveData<List<TargetEntity>> {
        Log.d("TargetRepository", "Fetching targets for userId: $userId")
        return targetDao.getTargetsByUserOwnerId(userId)
    }

    suspend fun updateCompletion(targetId: Int, completed: Boolean) {
        targetDao.updateCompletion(targetId, completed)
    }
}
