package com.theandroiddev.riffking.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by jakub on 21.09.17.
 */

object Utility {

    fun isNetworkAvailable(context: Context): Boolean {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netinfo = cm.activeNetworkInfo

        if (netinfo != null && netinfo.isConnectedOrConnecting) {
            val wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            return mobile != null && mobile.isConnectedOrConnecting || wifi != null && wifi.isConnectedOrConnecting
        } else
            return false

    }

}