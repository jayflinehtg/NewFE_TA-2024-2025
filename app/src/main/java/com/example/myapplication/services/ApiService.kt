package com.example.myapplication.services

import com.example.myapplication.data.DataClassResponses.AddPlantRequest
import com.example.myapplication.data.DataClassResponses.AddPlantResponse
import com.example.myapplication.data.DataClassResponses.LoginResponse
import com.example.myapplication.data.DataClassResponses.LogoutResponse
import com.example.myapplication.data.DataClassResponses.RegisterResponse
import com.example.myapplication.data.DataClassResponses.UserInfoResponse
import com.example.myapplication.data.IPFSResponse
import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.User
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /* ================================ Autentikasi ================================ */
    @POST("auth/register")
    fun registerUser(@Body user: User): Call<RegisterResponse>

    @POST("auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("auth/user/{walletAddress}")
    suspend fun getUserInfo(@Path("walletAddress") walletAddress: String): UserInfoResponse

    @POST("auth/logout")
    fun logoutUser(@Header("Authorization") authorization: String): Call<LogoutResponse>

    /* ================================ Tanaman ================================ */
    @POST("plants/add")
    fun addPlant(
        @Header("Authorization") token: String,
        @Body request: AddPlantRequest
    ): Call<AddPlantResponse>

    /* ================================ IPFS ================================ */
    @Multipart
    @POST("ipfs/upload")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<IPFSResponse>
}
