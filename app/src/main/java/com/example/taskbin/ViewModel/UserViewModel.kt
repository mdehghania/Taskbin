package com.example.taskbin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskbin.Model.UserEntity
import com.example.taskbin.Repository.UserRepository
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userByUsername = MutableLiveData<UserEntity?>()
    val userByUsername: LiveData<UserEntity?> get() = _userByUsername

    fun insert(user: UserEntity) = viewModelScope.launch {
        repository.insert(user)
    }

    fun getUser(username: String, password: String, callback: (UserEntity?) -> Unit) = viewModelScope.launch {
        val user = repository.getUser(username, password)
        callback(user)
    }

    fun getUserByUsername(username: String, callback: (UserEntity?) -> Unit) = viewModelScope.launch {
       val user = repository.getUserByUsername(username)
    }

    fun fetchUserByUsername(username: String) = viewModelScope.launch {
        val user = repository.getUserByUsername(username)
        _userByUsername.postValue(user)
    }

    fun getAnyUser(callback: (UserEntity?) -> Unit) = viewModelScope.launch {
        val user = repository.getAnyUser()
        callback(user)
    }
}