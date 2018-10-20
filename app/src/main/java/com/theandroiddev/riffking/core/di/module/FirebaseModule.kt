package com.theandroiddev.riffking.core.di.module

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.theandroiddev.riffking.R
import com.theandroiddev.riffking.utils.Utility
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(context: Context): GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

    //TODO export string
    @Provides
    @Singleton
    fun provideGoogleApiClient(context: Context, gso: GoogleSignInOptions): GoogleApiClient = GoogleApiClient.Builder(context)
            .enableAutoManage(context as FragmentActivity) {
                Toast.makeText(context, "Error Google API Connection", Toast.LENGTH_SHORT).show()
            }
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

    @Provides
    @Singleton
    fun provideUtility(context: Context): Utility = Utility(context)

}