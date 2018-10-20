package com.theandroiddev.riffking.core

import android.support.design.widget.Snackbar
import android.support.v4.app.FragmentActivity

class Messages(val activity: FragmentActivity) {

    fun showSnackbar(message: String) {
        Snackbar.make(activity.window.decorView.rootView, message, Snackbar.LENGTH_LONG).show()
    }
}