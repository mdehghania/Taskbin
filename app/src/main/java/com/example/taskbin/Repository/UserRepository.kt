package com.example.taskbin.Repository

import com.example.taskbin.Dao.UserDao
import com.example.taskbin.Model.UserEntity

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: UserEntity) {
        val existingUser = userDao.getUserByUsername(user.username)
        if (existingUser == null) {
            userDao.insert(user)
        } else {
            userDao.update(user.copy(userId = existingUser.userId))
        }
    }

    suspend fun getUser(username: String, password: String): UserEntity? {
        return userDao.getUser(username, password)
    }

    suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getAnyUser(): UserEntity? {
        return userDao.getAnyUser()
    }
}
