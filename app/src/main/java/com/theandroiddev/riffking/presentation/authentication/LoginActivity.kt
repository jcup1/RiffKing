package com.theandroiddev.riffking.presentation.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.core.Messages
import com.theandroiddev.riffking.core.mvp.MvpAppCompatActivity
import com.theandroiddev.riffking.presentation.home.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : MvpAppCompatActivity<LoginView, LoginPresenter>(), LoginView {

    override val messages: Messages = Messages(this)

    override val layoutResId: Int?
        get() = R.layout.activity_login

    private var progress: ProgressDialog? = null

    override fun startHomeActivity() {
        val i = Intent(this@LoginActivity, HomeActivity::class.java)
        //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
        startActivity(i)
        finish()
    }

    override fun finishLogin() {
        finish()
    }

    override fun finishAsUser(signInIntent: Intent) {
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun continueAsGuest() {
        val i = Intent(this@LoginActivity, HomeActivity::class.java)
        i.putExtra("guestmode", true)
        startActivity(i)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_guest.setOnClickListener {
            continueAsGuest()
        }

        signInButton.setOnClickListener {
            presenter.continueAsUser()
        }

        //TODO finish ProgressDialog
        progress = ProgressDialog(applicationContext)
        progress?.setTitle("Processing...")
        progress?.setMessage("Please wait...")
        progress?.setCancelable(false)
        progress?.isIndeterminate = true

    }

    public override fun onPause() {
        super.onPause()

        presenter.onPause()
        presenter.googleApiClient.stopAutoManage(this@LoginActivity)
        presenter.googleApiClient.disconnect()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount

                if (account != null) {
                    presenter.firebaseAuthWithGoogle(account)
                }


            } else {
                Toast.makeText(this, "Firebase Auth Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private val TAG = "LoginActivity"
        private val RC_SIGN_IN = 2
    }


}
