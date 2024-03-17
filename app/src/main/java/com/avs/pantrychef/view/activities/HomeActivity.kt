package com.avs.pantrychef.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

            val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigation)

            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_home -> {
                        loadFragment(HomeFragment())
                        true
                    }

                    R.id.navigation_shopping -> {
                        loadFragment(ShoppingListFragment())
                        true
                    }

                    R.id.navigation_favs -> {
                        loadFragment(FavsFragment())
                        true
                    }

                    else -> false
                }
            }

            if (savedInstanceState == null) {
                loadFragment(HomeFragment())
                bottomNavigationView.selectedItemId = R.id.navigation_home
            }
        }

        private fun loadFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit()
        }
}