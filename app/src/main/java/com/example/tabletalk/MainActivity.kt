package com.example.tabletalk

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.tabletalk.data.repositories.AuthListener
import com.example.tabletalk.data.repositories.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    var navController: NavController? = null
    var previousIsLogged: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHostFragment: NavHostFragment? =
            supportFragmentManager.findFragmentById(R.id.main_nav_host) as? NavHostFragment
        navController = navHostFragment?.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_bar)
        navController?.let { NavigationUI.setupWithNavController(bottomNavigationView, it) }

        if (UserRepository.getInstance().isLogged()) {
            bottomNavigationView.visibility = View.VISIBLE
        }

        UserRepository.getInstance().addAuthStateListener(object : AuthListener {
            override fun onAuthStateChanged() {
                if (previousIsLogged == UserRepository.getInstance().isLogged()) return

                previousIsLogged = UserRepository.getInstance().isLogged()
                if (previousIsLogged == true) {
                    bottomNavigationView.visibility = View.VISIBLE
                    navController?.navigate(R.id.postsListFragment)
                } else {
                    bottomNavigationView.visibility = View.GONE
                    navController?.navigate(R.id.loginFragment)
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            navController?.popBackStack()
            return true
        }

        return NavigationUI.onNavDestinationSelected(item, navController as NavController)
    }
}