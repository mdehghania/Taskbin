package com.example.taskbin.View

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.taskbin.MyApplication
import com.example.taskbin.R
import com.example.taskbin.ViewModel.UserViewModel
import com.example.taskbin.ViewModel.ViewModelFactory
import java.util.Locale
import java.util.concurrent.Executor

class login : AppCompatActivity() {
    private lateinit var userNameEditText: EditText
    private lateinit var userPassEditText: EditText
    private lateinit var btnNextLogin: ImageButton
    private lateinit var btnFingerprintLogin: ImageButton
    private val userViewModel: UserViewModel by viewModels {
        ViewModelFactory((application as MyApplication).userRepository)
    }
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var executor: Executor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setLocaleAndDirection()
        setContentView(R.layout.activity_login)

        userNameEditText = findViewById(R.id.edittext)
        userPassEditText = findViewById(R.id.editTextText)
        btnNextLogin = findViewById(R.id.btnNextLogin)
        btnFingerprintLogin = findViewById(R.id.btnFingerprintLogin)

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val savedUsername = sharedPreferences.getString("username", "")
        userNameEditText.setText(savedUsername)

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(applicationContext, "خطا احراز هویت: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(applicationContext, "احراز هویت با موفقیت انجام شد!", Toast.LENGTH_SHORT).show()
                val userName = userNameEditText.text.toString()
                if (userName.isNotEmpty()) {
                    userViewModel.getUserByUsername(userName) { user ->
                        if (user != null) {
                            val editor = sharedPreferences.edit()
                            editor.putString("username", userName)
                            editor.putInt("userOwnerId", user.userId)
                            editor.apply()

                            val intent = Intent(this@login, MainActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        } else {
                            Toast.makeText(applicationContext, "کاربر وارد نشد", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "لطفا نام کاربری خود را وارد نمایید", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "احراز هویت ناموفق بود", Toast.LENGTH_SHORT).show()
            }
        })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود اثر انگشت برای برنامه من")
            .setSubtitle("با استفاده از اثر انگشت خود وارد شوید")
            .setNegativeButtonText("از رمز عبور حساب کاربری استفاده کنید")
            .build()

        btnFingerprintLogin.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        btnNextLogin.setOnClickListener {
            val userName = userNameEditText.text.toString()
            val userPass = userPassEditText.text.toString()

            if (userName.isEmpty() || userPass.isEmpty()) {
                Toast.makeText(this, "لطفا هر دو فیلد را پر کنید", Toast.LENGTH_SHORT).show()
            } else {
                userViewModel.getUser(userName, userPass) { user ->
                    if (user != null) {
                        val editor = sharedPreferences.edit()
                        editor.putString("username", userName)
                        editor.putInt("userOwnerId", user.userId)
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

    private fun setLocaleAndDirection() {
        val locale = Locale("en")
        Locale.setDefault(locale)
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(Locale("en"))
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
