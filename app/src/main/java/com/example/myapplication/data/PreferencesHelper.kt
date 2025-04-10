package com.example.myapplication.data

import android.content.Context

object PreferencesHelper {

    private const val PREFS_NAME = "AppPreferences"
    private const val KEY_IS_META_MASK_CONNECTED = "isMetaMaskConnected"
    private const val KEY_IS_USER_REGISTERED = "isUserRegistered"

    // Simpan status koneksi MetaMask
    fun saveMetaMaskConnectionStatus(context: Context, isConnected: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_META_MASK_CONNECTED, isConnected)
            .apply()
    }

    // Cek status koneksi MetaMask
    fun isMetaMaskConnected(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_META_MASK_CONNECTED, false)
    }

    // Simpan status pendaftaran user
    fun saveUserRegistrationStatus(context: Context, isRegistered: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_USER_REGISTERED, isRegistered)
            .apply()
    }

    // Cek status pendaftaran user
    fun isUserRegistered(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_USER_REGISTERED, false)
    }
}
