package com.example.taskbin.Repository

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

    suspend fun getTargetsByUser(userId: Int): List<TargetEntity> {
        return targetDao.getTargetsByUser(userId)
    }
}