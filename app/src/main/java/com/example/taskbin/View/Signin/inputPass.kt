package com.example.taskbin.View.Signin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.View.login
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class inputPass : AppCompatActivity() {
    private lateinit var passEditText: EditText
    private lateinit var returnPassEditText: EditText
    private lateinit var btnNextPass: ImageButton
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_pass)

        passEditText = findViewById(R.id.txtPass)
        returnPassEditText = findViewById(R.id.txtRerutnPass)
        btnNextPass = findViewById(R.id.btnNextPass)

        val userName = intent.getStringExtra("username")

        btnNextPass.setOnClickListener {
            val password = passEditText.text.toString()
            val returnPassword = returnPassEditText.text.toString()

            if (password.isEmpty() || returnPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else if (password != returnPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.fetchUserByUsername(userName ?: "")
                userViewModel.userByUsername.observe(this, Observer { user ->
                    user?.let {
                        val updatedUser = it.copy(userPassword = password)
                        userViewModel.insert(updatedUser)
                        val intent = Intent(this, login::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                })
            }
        }
    }
}