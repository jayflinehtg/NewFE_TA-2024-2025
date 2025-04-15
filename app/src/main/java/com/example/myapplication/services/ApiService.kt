package com.example.myapplication.services

import com.example.myapplication.data.DataClassResponses.LoginResponse
import com.example.myapplication.data.DataClassResponses.LogoutResponse
import com.example.myapplication.data.DataClassResponses.RegisterResponse
import com.example.myapplication.data.DataClassResponses.UserInfoResponse
import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

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

    // mendapatkan data user
    @GET("auth/user/{walletAddress}")
    suspend fun getUserInfo(
        @Path("walletAddress") walletAddress: String
    ): UserInfoResponse

    // Endpoint untuk logout pengguna
    @POST("auth/logout")
    fun logoutUser(
        @Header("Authorization") authorization: String
    ): Call<LogoutResponse>
}