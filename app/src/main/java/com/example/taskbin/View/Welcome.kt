package com.example.taskbin.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.R
import com.example.taskbin.View.Signin.inputUserName

class Welcome : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnStart: Button = findViewById(R.id.btnStart)
        btnStart.setOnClickListener {
            val intent = Intent(this, inputUserName::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val txtAccount:TextView = findViewById(R.id.txtAccount)
        txtAccount.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }
}