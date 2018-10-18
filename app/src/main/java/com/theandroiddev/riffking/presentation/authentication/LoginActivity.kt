package com.theandroiddev.riffking.presentation.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.presentation.common.User
import com.theandroiddev.riffking.presentation.home.HomeActivity
import com.theandroiddev.riffking.utils.Utility
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    internal var firebaseAuth: FirebaseAuth? = null
    internal var googleApiClient: GoogleApiClient? = null
    internal var authListener: FirebaseAuth.AuthStateListener

    private var progress: ProgressDialog? = null
    private var database: DatabaseReference? = null

    init {
        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                val i = Intent(this@LoginActivity, HomeActivity::class.java)
                //i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                startActivity(i)
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        initPostData()
    }

    private fun initPostData() {
        if (Utility.isNetworkAvailable(applicationContext)) {
            firebaseAuth?.addAuthStateListener(authListener)
            database = FirebaseDatabase.getInstance().reference
        }
    }

    @OnClick(R.id.login_guest)
    fun guest() {
        val i = Intent(this@LoginActivity, HomeActivity::class.java)
        i.putExtra("guestmode", true)
        startActivity(i)
        finish()
    }

    @OnClick(R.id.signInButton)
    fun login() {
        if (Utility.isNetworkAvailable(applicationContext)) {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else
            noInternetSnack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        //TODO finish ProgressDialog
        progress = ProgressDialog(applicationContext)
        progress?.setTitle("Processing...")
        progress?.setMessage("Please wait...")
        progress?.setCancelable(false)
        progress?.isIndeterminate = true

        if (Utility.isNetworkAvailable(applicationContext)) {

            initData()
        } else {
            noInternetSnack()
        }

    }

    private fun initData() {

        firebaseAuth = FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { Toast.makeText(this@LoginActivity, "Error Google API Connection", Toast.LENGTH_SHORT).show() }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private fun noInternetSnack() {

        val snack = Snackbar.make(login_layout, "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("REFRESH") {
                    if (Utility.isNetworkAvailable(applicationContext)) {
                        //Ifs to handle the case when user opened app with internet connection and lost it after onStart()
                        //If not null, then already managing a GoogleApiClient
                        if (firebaseAuth == null)
                            initData()
                        if (database == null)
                            initPostData()
                    } else
                        noInternetSnack()
                }
        snack.show()
    }

    public override fun onPause() {
        super.onPause()

        googleApiClient?.stopAutoManage(this@LoginActivity)
        googleApiClient?.disconnect()
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
                    firebaseAuthWithGoogle(account)
                }


            } else {
                Toast.makeText(this, "Firebase Auth Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth?.signInWithCredential(credential)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "onActivityResult: accnt not null")
                        val user = User(account.displayName
                                ?: "N/A",
                                account.email ?: "N/A",
                                account.photoUrl.toString(), 0, 0, 0, 0, 0)
                        val currentUserUid = firebaseAuth?.currentUser?.uid
                        if(currentUserUid != null){
                            writeNewUser(user, currentUserUid)
                            Log.d(TAG, "signInWithCredential:success")
                        }
                        finish()
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        //updateUI(null);
                    }

                    // ...
                }
    }

    private fun writeNewUser(user: User, id: String) {

        database?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("users").child(id).getValue(User::class.java) == null) {
                    database?.child("users")?.child(id)?.setValue(user)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    companion object {
        private val TAG = "LoginActivity"
        private val RC_SIGN_IN = 2
    }


}
