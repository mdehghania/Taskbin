package com.example.taskbin.View

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class login : AppCompatActivity() {
    private lateinit var userNameEditText: EditText
    private lateinit var userPassEditText: EditText
    private lateinit var btnNextLogin: ImageButton
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)  // مطمئن شوید که نام فایل XML صحیح است

        userNameEditText = findViewById(R.id.edittext)  // مطمئن شوید که شناسه صحیح است
        userPassEditText = findViewById(R.id.editTextText)  // مطمئن شوید که شناسه صحیح است
        btnNextLogin = findViewById(R.id.btnNextLogin)  // مطمئن شوید که شناسه صحیح است

        btnNextLogin.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val userPass = userPassEditText.text.toString()

            if (userName.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.getUser(userName, userPass) { user ->
                    if (user != null) {
                        // ذخیره نام کاربری در SharedPreferences
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("username", userName)
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

                    } else {
                        Toast.makeText(this, "نام کاربری یا رمز عبور نادرست است!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
