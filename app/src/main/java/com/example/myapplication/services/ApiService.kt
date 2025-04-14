package com.example.myapplication.services

import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    // Endpoint untuk registrasi pengguna
    @POST("auth/register")
    fun registerUser(
        @Body user: User
    ): Call<RegisterResponse>

    // Endpoint untuk login pengguna
    @POST("auth/login")
    fun loginUser(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>
}

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
