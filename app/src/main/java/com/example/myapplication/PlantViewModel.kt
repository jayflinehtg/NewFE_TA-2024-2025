package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.DataClassResponses.AddPlantRequest
import com.example.myapplication.data.DataClassResponses.AddPlantResponse
import com.example.myapplication.data.DataClassResponses.RatedPlant
import com.example.myapplication.data.PlantResponse
import com.example.myapplication.data.PaginatedPlantResponse
import com.example.myapplication.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PlantViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    // ==================== CID ====================
    private val _cid = mutableStateOf("")
    val cid: State<String> = _cid

    fun setCid(newCid: String) {
        _cid.value = newCid
    }

    // ==================== Tambah Tanaman ====================
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

    // ==================== Search Tanaman ========================
    fun searchPlants(
        name: String = "",
        namaLatin: String = "",
        komposisi: String = "",
        kegunaan: String = ""
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.searchPlants(name, namaLatin, komposisi, kegunaan)
                val ratedResults = response.plants.map { plant ->
                    val ratingResponse = try {
                        apiService.getAverageRating(plant.id)
                    } catch (e: Exception) {
                        null
                    }
                    val avgRating = ratingResponse?.averageRating?.toDoubleOrNull() ?: 0.0
                    RatedPlant(plant, avgRating)
                }
                _ratedPlantList.value = ratedResults
                _totalPlants.value = ratedResults.size
                _currentPage.value = 1
                Log.d("SearchPlant", "Berhasil mencari ${ratedResults.size} tanaman.")
            } catch (e: Exception) {
                Log.e("SearchPlant", "Gagal mencari tanaman: ${e.message}")
            }
        }
    }

    // ==================== Pagination Tanaman ====================
    private val _ratedPlantList = mutableStateOf<List<RatedPlant>>(emptyList())
    val ratedPlantList: State<List<RatedPlant>> = _ratedPlantList

    private val _totalPlants = mutableStateOf(0)
    val totalPlants: State<Int> = _totalPlants

    private val _currentPage = mutableStateOf(1)
    val currentPage: State<Int> = _currentPage

    private val pageSize = 10

    fun fetchPlantsByPage(page: Int = 1) {
        viewModelScope.launch {
            try {
                Log.d("Pagination", "Fetching page $page")
                val response: PaginatedPlantResponse = apiService.getPaginatedPlants(page, pageSize)
                if (response.success) {
                    val ratedPlants = response.plants.map { plant ->
                        val ratingResponse = try {
                            apiService.getAverageRating(plant.id)
                        } catch (e: Exception) {
                            null
                        }
                        val avgRating = ratingResponse?.averageRating?.toDoubleOrNull() ?: 0.0
                        RatedPlant(plant, avgRating)
                    }

                    _ratedPlantList.value = ratedPlants
                    _totalPlants.value = response.total.toInt()
                    _currentPage.value = response.currentPage
                    Log.d("Pagination", "Fetched ${ratedPlants.size} rated plants on page $page")
                } else {
                    Log.e("Pagination", "Failed to fetch plants: success=false")
                }
            } catch (e: Exception) {
                Log.e("Pagination", "Error fetching plants: ${e.message}")
            }
        }
    }
}
