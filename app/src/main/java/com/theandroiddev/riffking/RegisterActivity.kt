package com.theandroiddev.riffking

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private var email: String? = null
    private var password: String? = null
    private var name: String? = null

    @OnClick(com.theandroiddev.riffking.R.id.register_register_btn)
    fun register() {

        //tryToRegister(register_email_et, register_password_et);

    }

    @OnClick(com.theandroiddev.riffking.R.id.register_login_tv)
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
        val intent: Intent
        intent = Intent(this@RegisterActivity, HomeActivity::class.java)
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
        setContentView(com.theandroiddev.riffking.R.layout.activity_register)

        ButterKnife.bind(this)


    }

    companion object {

        private val TAG = "RegisterActivity"
    }
}
