package com.example.taskbin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.UserEntity
import com.example.taskbin.Repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    fun insert(user: UserEntity) = viewModelScope.launch {
        repository.insert(user)
    }

    fun getUser(username: String, password: String, callback: (UserEntity?) -> Unit) = viewModelScope.launch {
        val user = repository.getUser(username, password)
        callback(user)
    }
}