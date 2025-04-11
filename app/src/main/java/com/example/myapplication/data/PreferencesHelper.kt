package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {

    private const val PREFS_NAME = "AppPreferences"
    private const val KEY_IS_META_MASK_CONNECTED = "isMetaMaskConnected"
    private const val KEY_IS_USER_REGISTERED = "isUserRegistered"
    private const val KEY_WALLET_ADDRESS = "walletAddress"

    // Fungsi helper untuk mendapatkan SharedPreferences
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

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

    // Simpan alamat wallet
    fun saveWalletAddress(context: Context, walletAddress: String) {
        getPrefs(context).edit().putString(KEY_WALLET_ADDRESS, walletAddress).apply()
    }

    // Ambil alamat wallet
    fun getWalletAddress(context: Context): String? {
        return getPrefs(context).getString(KEY_WALLET_ADDRESS, null)
    }
}
