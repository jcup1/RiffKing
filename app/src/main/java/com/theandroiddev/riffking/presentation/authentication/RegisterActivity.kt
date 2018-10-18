package com.theandroiddev.riffking.presentation.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.home.HomeActivity
import com.theandroiddev.riffking.utils.SharedPrefManager
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private var email: String? = null
    private var password: String? = null
    private var name: String? = null

    fun register() {

        //tryToRegister(register_email_et, register_password_et);

    }

    fun login() {

        startLoginActivity()
    }

    private fun onRegisterFailed() {

        Toast.makeText(this, "Register Failed", Toast.LENGTH_SHORT).show()
        register_register_btn.isEnabled = true

    }

    private fun validate(): Boolean {

        return true
    }

    private fun onEmailNotAvalible() {

        register_email_et.error = "Email is in use"
        register_register_btn.isEnabled = true
    }

    private fun onRegisterSuccess(id: Int, email: String) {
        register_register_btn.isEnabled = true

        nextActivity(id, email)
        finish()
    }

    private fun nextActivity(id: Int, email: String) {

        SharedPrefManager(this).userLogin(id, email)
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        finish()
        startActivity(intent)

    }

    private fun startLoginActivity() {
        val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(loginIntent)
        finish()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register_register_btn.setOnClickListener {
            register()
        }

        register_login_tv.setOnClickListener {
            login()
        }

    }

    companion object {

        private val TAG = "RegisterActivity"
    }
}
