//RetrofitClient
package com.example.myapplication.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.3.202:5000/api/"  // Semua route, termasuk IPFS

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
