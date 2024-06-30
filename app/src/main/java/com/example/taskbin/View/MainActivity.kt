package com.example.taskbin.View


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.taskbin.Fragment.AddProjectFragment
import com.example.taskbin.Fragment.AddTargetFragment
import com.example.taskbin.Fragment.FolderFragment
import com.example.taskbin.Fragment.HomeFragment
import com.example.taskbin.Fragment.TrapgyFragment
import com.example.taskbin.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentFragmentId: Int = R.id.Home  // Default to HomeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize bottomNavigationView
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

        // Show HomeFragment by default
        replaceFragment(TrapgyFragment())

        // Register FragmentLifecycleCallbacks
        supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                super.onFragmentResumed(fm, f)
                if (f !is AddActivityFragment && f !is AddProjectFragment && f !is AddTargetFragment) {
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
}
