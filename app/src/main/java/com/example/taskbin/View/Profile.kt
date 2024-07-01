package com.example.taskbin.View

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.taskbin.Model.UserEntity
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory

class Profile : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var btnBackP: ImageButton
    private lateinit var btnLogout: ImageView
    private lateinit var editUserNametxt: TextView
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // مقداردهی ویوها
        userNameTextView = findViewById(R.id.userNametxt)
        btnBackP = findViewById(R.id.btnBackP)
        btnLogout = findViewById(R.id.btnLogout)
        editUserNametxt = findViewById(R.id.editUserNametxt)

        // بازیابی نام کاربری از SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userName = sharedPreferences.getString("username", "User")

        // بازیابی و نمایش نام کاربری از ViewModel
        userName?.let { name ->
            userViewModel.getUserByUsername(name) { user ->
                user?.let {
                    userNameTextView.text = it.username
                }
            }
        }

        // مشاهده تغییرات در داده‌های کاربر
        userViewModel.userByUsername.observe(this, Observer { user: UserEntity? ->
            user?.let {
                userNameTextView.text = it.username
            }
        })

        // تنظیم کلیک برای ویرایش نام کاربری
        editUserNametxt.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_profile_layout, null)
            val inputNewPass = dialogView.findViewById<EditText>(R.id.inputNewPass)
            val inputNewPhone = dialogView.findViewById<EditText>(R.id.inputNewPhone)
            val inputNewUserName = dialogView.findViewById<EditText>(R.id.inputNewUserName)
            val dialogPositiveButton = dialogView.findViewById<Button>(R.id.btnSaveProfileChange)
            val dialogNegativeButton = dialogView.findViewById<Button>(R.id.btnCancleProfileChane)

            val dialogBuilder = AlertDialog.Builder(this)
                .setView(dialogView)
                .create()

            // Set background of the dialog to transparent to ensure rounded corners
            dialogBuilder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialogPositiveButton.setOnClickListener {
                val newPassword = inputNewPass.text.toString()
                val newPhone = inputNewPhone.text.toString()
                val newUserName = inputNewUserName.text.toString()

                if (newPassword.isBlank() && newPhone.isBlank() && newUserName.isBlank()) {
                    Toast.makeText(this, "هیچ مقداری وارد نشده است", Toast.LENGTH_SHORT).show()
                } else {
                    updateUser(userName!!, newPassword, newPhone, newUserName)
                    dialogBuilder.dismiss()
                }
            }

            dialogNegativeButton.setOnClickListener {
                dialogBuilder.dismiss()
            }

            dialogBuilder.show()
        }

        // تنظیم کلیک برای دکمه بازگشت
        btnBackP.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // تنظیم کلیک برای دکمه خروج
        btnLogout.setOnClickListener {
            val intent = Intent(this, Welcome::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun updateUser(userName: String, newPassword: String, newPhone: String, newUserName: String) {
        userViewModel.getUserByUsername(userName) { user ->
            user?.let {
                val updatedUser = it.copy(
                    userPassword = if (newPassword.isNotBlank()) newPassword else it.userPassword,
                    userPhone = if (newPhone.isNotBlank()) newPhone else it.userPhone,
                    username = if (newUserName.isNotBlank()) newUserName else it.username
                )
                userViewModel.insert(updatedUser)

                // افزودن Toast داخل فراخوانی viewModel برای اطمینان از آپدیت شدن داده‌ها
                runOnUiThread {
                    Toast.makeText(this, "اطلاعات با موفقیت به‌روزرسانی شد", Toast.LENGTH_SHORT).show()
                }

                // به‌روزرسانی SharedPreferences برای نام کاربری جدید
                if (newUserName.isNotBlank()) {
                    val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("username", newUserName)
                        apply()
                    }

                    // به‌روزرسانی مستقیم TextView
                    runOnUiThread {
                        userNameTextView.text = newUserName
                    }
                }
            } ?: runOnUiThread {
                Toast.makeText(this, "کاربر یافت نشد", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
