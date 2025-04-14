package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object PreferencesHelper {

    private const val PREFS_NAME = "AppPreferences"
    private const val KEY_IS_META_MASK_CONNECTED = "isMetaMaskConnected"
    private const val KEY_IS_USER_REGISTERED = "isUserRegistered"
    private const val KEY_WALLET_ADDRESS = "walletAddress"
    private const val KEY_JWT_TOKEN = "jwtToken" // Tambahkan key untuk JWT Token

    // Fungsi helper untuk mendapatkan SharedPreferences
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Simpan status koneksi MetaMask
    fun saveMetaMaskConnectionStatus(context: Context, isConnected: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_IS_META_MASK_CONNECTED, isConnected)
        }
    }

    // Cek status koneksi MetaMask
    fun isMetaMaskConnected(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_META_MASK_CONNECTED, false)
    }

    // Simpan status pendaftaran user
    fun saveUserRegistrationStatus(context: Context, isRegistered: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_IS_USER_REGISTERED, isRegistered)
        }
    }

    // Cek status pendaftaran user
    fun isUserRegistered(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_IS_USER_REGISTERED, false)
    }

    // Simpan alamat wallet
    fun saveWalletAddress(context: Context, walletAddress: String) {
        getPrefs(context).edit { putString(KEY_WALLET_ADDRESS, walletAddress) }
    }

    // Ambil alamat wallet
    fun getWalletAddress(context: Context): String? {
        return getPrefs(context).getString(KEY_WALLET_ADDRESS, null)
    }

    // **Tambahkan fungsi untuk menyimpan token JWT**
    fun saveJwtToken(context: Context, jwtToken: String) {
        getPrefs(context).edit { putString(KEY_JWT_TOKEN, jwtToken) }
    }

    // **Tambahkan fungsi untuk mengambil token JWT**
    fun getJwtToken(context: Context): String? {
        return getPrefs(context).getString(KEY_JWT_TOKEN, null)
    }

    // **Tambahkan fungsi untuk menghapus token JWT (opsional)**
    fun clearJwtToken(context: Context) {
        getPrefs(context).edit { remove(KEY_JWT_TOKEN) }
    }
}
