package com.theandroiddev.riffking.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.theandroiddev.riffking.presentation.authentication.LoginActivity

/**
 * Created by jakub on 07.10.17.
 */

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}
