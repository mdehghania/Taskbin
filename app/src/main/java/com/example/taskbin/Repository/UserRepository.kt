package com.example.taskbin.Repository

import com.example.taskbin.Dao.UserDao
import com.example.taskbin.Model.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: UserEntity) {
        userDao.insert(user)
    }

    suspend fun getUser(username: String, password: String): UserEntity? {
        return userDao.getUser(username, password)
    }
}