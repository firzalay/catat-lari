package com.upn.catatlari.view.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.upn.catatlari.R
import com.upn.catatlari.model.User

class MainActivity : AppCompatActivity() {

    var user: User? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        user = intent.getParcelableExtra("user", User::class.java)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val rootView = findViewById<ConstraintLayout>(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())


            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

            insets
        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigation.setupWithNavController(navController)


        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigation) { view, insets ->
            val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())


            view.setPadding(0, 0, 0, navInsets.bottom / 2)

            insets
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.registerFragment -> bottomNavigation.visibility = View.GONE
                else -> bottomNavigation.visibility = View.VISIBLE
            }
        }
    }
}