package com.avs.pantrychef.view.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.avs.pantrychef.R
import com.avs.pantrychef.controller.AuthController

class HomeActivity: AppCompatActivity() {

        private lateinit var authController: AuthController

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_home)

            authController = AuthController(this)
        }

}