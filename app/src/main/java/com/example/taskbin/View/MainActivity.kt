package com.example.taskbin.View

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.Dao.AppDatabase
import com.example.taskbin.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getDatabase(this)




    }

}

