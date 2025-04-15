package com.example.myapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DataClassResponses.AddPlantRequest
import com.example.myapplication.data.DataClassResponses.AddPlantResponse
import com.example.myapplication.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    fun addPlant(
        token: String,
        request: AddPlantRequest,
        onSuccess: (AddPlantResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Panggil API secara sinkron di background thread
                val response = apiService.addPlant(token, request).execute()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("Add Plant", "Tanaman berhasil ditambahkan: ${it.message}")
                        onSuccess(it)
                    } ?: onError("Response body kosong")
                } else {
                    Log.e("Add Plant", "Gagal menambahkan tanaman: ${response.message()}")
                    onError("Gagal menambahkan tanaman, coba lagi.")
                }
            } catch (e: IOException) {
                Log.e("Add Plant", "Error menambahkan tanaman: $e")
                onError("Terjadi kesalahan jaringan: ${e.message}")
            } catch (e: Exception) {
                Log.e("Add Plant", "Error tidak terduga: $e")
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}
