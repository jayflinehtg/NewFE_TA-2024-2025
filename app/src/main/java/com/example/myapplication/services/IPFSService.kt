package com.example.myapplication.services

import com.example.myapplication.data.IPFSResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface IPFSService {
    // Endpoint untuk mengupload gambar ke IPFS
    @Multipart
    @POST("ipfs/upload")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Call<IPFSResponse>
}
