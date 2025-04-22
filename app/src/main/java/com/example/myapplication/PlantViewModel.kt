package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    // Menyimpan CID hasil upload
    private val _cid = mutableStateOf("")
    val cid: State<String> = _cid // Memastikan akses State yang benar

    // Fungsi untuk menyetel CID
    fun setCid(newCid: String) {
        _cid.value = newCid
    }

    // Fungsi untuk menambahkan tanaman
    fun addPlant(
        token: String,
        request: AddPlantRequest,
        onSuccess: (AddPlantResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("AddPlant", "Sending request to backend with: $request")
                Log.d("AddPlant", "Authorization Token: $token")

                val response = apiService.addPlant(token, request)
                Log.d("AddPlant", "Success! Response: ${response.message}")
                onSuccess(response)

            } catch (e: IOException) {
                Log.e("AddPlant", "IOException: ${e.message}", e)
                onError("Terjadi kesalahan jaringan: ${e.message}")
            } catch (e: Exception) {
                Log.e("AddPlant", "Exception: ${e.message}", e)
                onError("Terjadi kesalahan: ${e.message}")
            }
        }
    }
}
