package com.example.taskbin.View

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.R

class Profile : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var btnBackP: ImageButton
    private lateinit var btnLogout: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // مقداردهی ویوها
        userNameTextView = findViewById(R.id.userNamePtxt)
        btnBackP = findViewById(R.id.btnBackP)
        btnLogout = findViewById(R.id.btnLogout)

        // بازیابی نام کاربری از SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "User")

        // نمایش نام کاربری در TextView
        userNameTextView.text = "$userName"

        // تنظیم کلیک برای دکمه بازگشت
        btnBackP.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
