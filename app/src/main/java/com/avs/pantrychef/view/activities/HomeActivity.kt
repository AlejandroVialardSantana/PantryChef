package com.avs.pantrychef.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController
import com.avs.pantrychef.view.fragments.FavsFragment
import com.avs.pantrychef.view.fragments.HomeFragment
import com.avs.pantrychef.view.fragments.ShoppingListFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity: AppCompatActivity() {

        private lateinit var authController: AuthController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        authController = AuthController(this)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setupWithNavController(navController)
    }
}