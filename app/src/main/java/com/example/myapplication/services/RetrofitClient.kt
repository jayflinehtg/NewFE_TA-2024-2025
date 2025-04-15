//RetrofitClient
package com.example.myapplication.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.50.94:5000/api/"  // Ganti dengan URL API backend kamu
    private const val IPFS_URL = "http://192.168.50.94:5001/"  // Ganti dengan URL IPFS kamu

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    private val ipfsRetrofit = Retrofit.Builder()
        .baseUrl(IPFS_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val ipfsService: IPFSService = ipfsRetrofit.create(IPFSService::class.java)
}
