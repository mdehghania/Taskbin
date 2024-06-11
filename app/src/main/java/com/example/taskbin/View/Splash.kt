package com.example.taskbin.View

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class Splash : AppCompatActivity() {
    lateinit var iv_note: ImageView
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        iv_note = this.findViewById(R.id.iv_note)
        iv_note.alpha = 0f
        iv_note.animate().setDuration(2000).alpha(1f).withEndAction {
            checkUserAndNavigate()
        }
    }

    private fun checkUserAndNavigate() {
        userViewModel.getAnyUser { user ->
            val intent = if (user != null) {
                Intent(this, login::class.java)
            } else {
                Intent(this, Welcome::class.java)
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
