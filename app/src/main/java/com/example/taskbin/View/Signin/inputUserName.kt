package com.example.taskbin.View.Signin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.Model.UserEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory


class inputUserName : AppCompatActivity() {
    private lateinit var userNameEditText: EditText
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_user_name)

        userNameEditText = findViewById(R.id.txtUserName)
        val btnNext: ImageButton = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val userName = userNameEditText.text.toString()

            if (userName.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
            } else {
                val user = UserEntity(username = userName, userPhone = "", userPassword = "")
                userViewModel.insert(user)

                val intent = Intent(this, inputPhone::class.java)
                intent.putExtra("username", userName)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }
}