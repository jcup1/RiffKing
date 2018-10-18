package com.theandroiddev.riffking

import android.content.Context

class SharedPrefManager(val context: Context) {

    private val SHARED_PREF_NAME = "mysharedpref12"
    private val KEY_USER_EMAIL = "useremail"
    private val KEY_USER_ID = "userid"

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USER_EMAIL, null) != null
        }


    val email: String?
        get() {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USER_EMAIL, null)
        }

    val id: Int
        get() {
            val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(KEY_USER_ID, 0)
        }

    fun userLogin(id: Int, email: String): Boolean {

        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt(KEY_USER_ID, id)
        editor.putString(KEY_USER_EMAIL, email)

        editor.apply()

        return true
    }

    fun logout(): Boolean {
        val sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        return true
    }

}