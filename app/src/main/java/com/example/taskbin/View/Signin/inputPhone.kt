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
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class inputPhone : AppCompatActivity() {
    private lateinit var phoneEditText: EditText
    private lateinit var btnNextPhone: ImageButton
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_phone)

        phoneEditText = findViewById(R.id.txtPhone)
        btnNextPhone = findViewById(R.id.btnNextPhone)

        val userName = intent.getStringExtra("username")

        btnNextPhone.setOnClickListener {
            val userPhone = phoneEditText.text.toString()

            if (userPhone.isEmpty()) {
                Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.fetchUserByUsername(userName ?: "")
                userViewModel.userByUsername.observe(this, Observer { user ->
                    user?.let {
                        val updatedUser = it.copy(userPhone = userPhone)
                        userViewModel.insert(updatedUser)
                        val intent = Intent(this, inputPass::class.java)
                        intent.putExtra("username", userName)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                })
            }
        }
    }
}