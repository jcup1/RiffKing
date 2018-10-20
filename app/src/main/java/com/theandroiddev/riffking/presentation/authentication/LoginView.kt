package com.theandroiddev.riffking.presentation.authentication

import android.content.Intent
import com.theandroiddev.riffking.core.Messages
import com.theandroiddev.riffking.core.mvp.MvpView

interface LoginView : MvpView {

    fun finishLogin()

    fun finishAsUser(signInIntent: Intent)

    fun startHomeActivity()

    val messages: Messages

}