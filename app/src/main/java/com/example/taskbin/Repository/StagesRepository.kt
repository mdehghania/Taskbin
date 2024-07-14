// StagesRepository.kt
package com.example.taskbin.Repository

import androidx.lifecycle.LiveData
import com.example.taskbin.Dao.StagesDao
import com.example.taskbin.Model.StagesEntity

class StagesRepository(private val stagesDao: StagesDao) {
    fun getStagesByProjectId(projectId: Int): LiveData<List<StagesEntity>> {
        return stagesDao.getStagesByProjectId(projectId)
    }

    suspend fun updateStage(stage: StagesEntity) {
        stagesDao.updateStage(stage)
    }
}


