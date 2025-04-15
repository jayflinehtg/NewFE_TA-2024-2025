package com.example.myapplication.data

import com.google.gson.annotations.SerializedName

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

    data class UserData(
        @SerializedName("fullName") val fullName: String,
        @SerializedName("isRegistered") val isRegistered: Boolean,
        @SerializedName("isLoggedIn") val isLoggedIn: Boolean
    )

    data class UserInfoResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("userData") val userData: UserData
    )

    // Data Class untuk AddPlant Request
    data class AddPlantRequest(
        @SerializedName("name") val name: String,
        @SerializedName("namaLatin") val namaLatin: String,
        @SerializedName("komposisi") val komposisi: String,
        @SerializedName("kegunaan") val kegunaan: String,
        @SerializedName("caraPengolahan") val caraPengolahan: String,
        @SerializedName("ipfsHash") val ipfsHash: String
    )

    // Data Class untuk AddPlant Response
    data class AddPlantResponse(
        @SerializedName("success") val success: Boolean,
        @SerializedName("message") val message: String,
        @SerializedName("txHash") val txHash: String,
        @SerializedName("plantId") val plantId: String
    )
}