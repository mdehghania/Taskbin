package com.example.taskbin.View

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.taskbin.Fragment.AddProjectFragment
import com.example.taskbin.Fragment.AddTargetFragment
import com.example.taskbin.Fragment.FolderFragment
import com.example.taskbin.Fragment.HomeFragment
import com.example.taskbin.Fragment.TrapgyFragment
import com.example.taskbin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentFragmentId: Int = R.id.Tropht

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.backgrond)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val decor = window.decorView
                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }

        setLocaleAndDirection()

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    replaceFragment(HomeFragment())
                    currentFragmentId = R.id.Home
                    true
                }
                R.id.Folder -> {
                    replaceFragment(FolderFragment())
                    currentFragmentId = R.id.Folder
                    true
                }
                R.id.Tropht -> {
                    replaceFragment(TrapgyFragment())
                    currentFragmentId = R.id.Tropht
                    true
                }
                R.id.Add -> {
                    performAddAction()
                    true
                }
                else -> false
            }
        }

        replaceFragment(TrapgyFragment())

        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                if (f is AddProjectFragment || f is AddTargetFragment || f is AddActivityFragment) {
                    bottomNavigationView.visibility = BottomNavigationView.GONE
                } else {
                    bottomNavigationView.visibility = BottomNavigationView.VISIBLE
                }
            }
        }, true)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit()
    }

    private fun performAddAction() {
        when (currentFragmentId) {
            R.id.Home -> {
                openFullScreenFragment(AddActivityFragment())
            }
            R.id.Folder -> {
                openFullScreenFragment(AddProjectFragment())
            }
            R.id.Tropht -> {
                openFullScreenFragment(AddTargetFragment())
            }
        }
    }

    private fun openFullScreenFragment(fragment: Fragment) {
        bottomNavigationView.visibility = BottomNavigationView.GONE
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.frame_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            bottomNavigationView.visibility = BottomNavigationView.VISIBLE
            supportFragmentManager.executePendingTransactions()
            bottomNavigationView.selectedItemId = currentFragmentId
        } else {
            super.onBackPressed()
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
