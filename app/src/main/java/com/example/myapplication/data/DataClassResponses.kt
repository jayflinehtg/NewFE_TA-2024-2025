package com.example.myapplication.data

class DataClassResponses {

    // Data class untuk respons registrasi
    data class RegisterResponse(
        val success: Boolean,
        val txHash: String,
        val message: String
    )

    // Data class untuk respons login
    data class LoginResponse(
        val success: Boolean,
        val token: String,
        val message: String
    )

    data class LogoutResponse(
        val success: Boolean,
        val message: String,
        val txHash: String? = null
    )

    data class UserInfoResponse(
        val fullName: String,
        val isRegistered: Boolean,
        val isLoggedIn: Boolean
    )
}