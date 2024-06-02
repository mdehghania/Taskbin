package com.example.taskbin.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.taskbin.Dao.AppDatabase
import com.example.taskbin.Model.UserEntity
import com.example.taskbin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getDatabase(this)

        val user = UserEntity(username = "test", userPhone = "1234567890", userPassword = "password")

        CoroutineScope(Dispatchers.IO).launch {
            db.userDao().insert(user)
            val users = db.userDao().getUser("test", "password")
            Log.d("MainActivity", "User retrieved: $users")
        }
    }

}