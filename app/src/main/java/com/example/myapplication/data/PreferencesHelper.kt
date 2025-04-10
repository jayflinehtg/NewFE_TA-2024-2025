package com.example.myapplication.data

import android.content.Context

object PreferencesHelper {

    private const val PREFS_NAME = "AppPreferences"
    private const val KEY_IS_META_MASK_CONNECTED = "isMetaMaskConnected"
    private const val KEY_IS_USER_REGISTERED = "isUserRegistered"

    // Simpan status koneksi MetaMask
    fun saveMetaMaskConnectionStatus(context: Context, isConnected: Boolean) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(KEY_IS_META_MASK_CONNECTED, isConnected)
            apply()
        }
    }

    // Simpan status pendaftaran user
    fun saveUserRegistrationStatus(context: Context, isRegistered: Boolean) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(KEY_IS_USER_REGISTERED, isRegistered)
            apply()
        }
    }

    // Cek status koneksi MetaMask
    fun isMetaMaskConnected(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_IS_META_MASK_CONNECTED, false)
    }

    // Cek status pendaftaran user
    fun isUserRegistered(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(KEY_IS_USER_REGISTERED, false)
    }
}
