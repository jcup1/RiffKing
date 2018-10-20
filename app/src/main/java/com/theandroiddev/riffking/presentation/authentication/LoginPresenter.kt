package com.theandroiddev.riffking.presentation.authentication


import android.support.v4.app.FragmentActivity
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.theandroiddev.riffking.core.mvp.MvpPresenter
import com.theandroiddev.riffking.presentation.common.User
import com.theandroiddev.riffking.utils.Utility
import javax.inject.Inject

class LoginPresenter @Inject constructor(
        val firebaseAuth: FirebaseAuth,
        val googleApiClient: GoogleApiClient,
        val firebaseDatabase: FirebaseDatabase,
        val utility: Utility
) : MvpPresenter<LoginView>() {

    override fun attachView(view: LoginView) {
        super.attachView(view)

        firebaseAuth.addAuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                ifViewAttached { view ->
                    view.startHomeActivity()
                }

            }

        }
    }

    fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = User(account.displayName
                        ?: "N/A",
                        account.email ?: "N/A",
                        account.photoUrl.toString(), 0, 0, 0, 0, 0)
                val currentUserUid = firebaseAuth.currentUser?.uid
                if (currentUserUid != null) {
                    writeNewUser(user, currentUserUid)
                    d { "signInWithCredential:success" }
                }

                d { "onActivityResult: account not null" }
                ifViewAttached { view ->
                    view.finishLogin()
                }
                //updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                e { "signInWithCredential:failure ${task.exception}" }
                ifViewAttached { view ->
                    view.messages.showSnackbar("Authentication failed")
                }
                //updateUI(null);
            }

            // ...
        }
    }

    private fun writeNewUser(user: User, id: String) {

        firebaseDatabase.reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child("users").child(id).getValue(User::class.java) == null) {
                    firebaseDatabase.reference.child("users").child(id).setValue(user)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun continueAsUser() {
        if (utility.isNetworkAvailable()) {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)

            ifViewAttached { view ->
                view.finishAsUser(signInIntent)
            }
        } else {
            ifViewAttached { view ->
                view.messages.showSnackbar("No interneto")
            }
        }
    }

    fun onPause() {
        googleApiClient.stopAutoManage(googleApiClient.context as FragmentActivity)
        googleApiClient.disconnect()
    }

}