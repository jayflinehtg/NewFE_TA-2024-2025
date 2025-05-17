package com.example.myapplication.ui.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.PlantViewModel
import com.example.myapplication.R
import com.example.myapplication.data.DataClassResponses
import com.example.myapplication.data.IPFSResponse
import com.example.myapplication.data.PreferencesHelper
import com.example.myapplication.services.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditPlantScreen(
    navController: NavController,
    plantId: String,
    viewModel: PlantViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val plant = viewModel.selectedPlant.value

    // State untuk form, diisi dari data tanaman
    var namaTanaman by remember { mutableStateOf(plant?.name ?: "") }
    var namaLatin by remember { mutableStateOf(plant?.namaLatin ?: "") }
    var komposisi by remember { mutableStateOf(plant?.komposisi ?: "") }
    var kegunaan by remember { mutableStateOf(plant?.kegunaan ?: "") }
    var dosis by remember { mutableStateOf(plant?.dosis ?: "") }
    var caraPengolahan by remember { mutableStateOf(plant?.caraPengolahan ?: "") }
    var efekSamping by remember { mutableStateOf(plant?.efekSamping ?: "") }
    var ipfsHash by remember { mutableStateOf(plant?.ipfsHash ?: "") }

    // Gambar
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    // Untuk memilih gambar baru
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> gambarUri = uri }

    // Jika plant berubah, update field
    LaunchedEffect(plant) {
        plant?.let {
            namaTanaman = it.name
            namaLatin = it.namaLatin
            komposisi = it.komposisi
            kegunaan = it.kegunaan
            dosis = it.dosis
            caraPengolahan = it.caraPengolahan
            efekSamping = it.efekSamping
            ipfsHash = it.ipfsHash
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F1E9))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.plant),
                contentDescription = "Plant Logo",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Edit Data Tanaman",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2E7D32)
                )
                Text("Tanaman Herbal", color = Color(0xFF4CAF50))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                FormField("Nama Tanaman", namaTanaman, showError && namaTanaman.isBlank()) { namaTanaman = it }
                FormField("Nama Latin", namaLatin, showError && namaLatin.isBlank()) { namaLatin = it }
                FormField("Komposisi", komposisi, showError && komposisi.isBlank()) { komposisi = it }
                FormField("Kegunaan", kegunaan, showError && kegunaan.isBlank()) { kegunaan = it }
                FormField("Dosis", dosis, showError && dosis.isBlank()) { dosis = it }
                FormField("Cara Pengolahan", caraPengolahan, showError && caraPengolahan.isBlank()) { caraPengolahan = it }
                FormField("Efek Samping", efekSamping, showError && efekSamping.isBlank()) { efekSamping = it }

                Spacer(modifier = Modifier.height(12.dp))
                Text("Gambar Tanaman", color = Color.Black)
                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pilih Gambar Baru", color = Color.White)
                }

                // Preview gambar baru jika ada, jika tidak tampilkan gambar lama
                if (gambarUri != null) {
                    // tampilkan gambar baru
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preview Gambar Baru:", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(gambarUri),
                        contentDescription = "Preview Gambar Tanaman",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(4.dp)
                    )
                } else if (ipfsHash.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Gambar Saat Ini:", color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter("https://ipfs.io/ipfs/$ipfsHash"),
                        contentDescription = "Gambar Tanaman Lama",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (gambarUri == null) {
                            Toast.makeText(context, "Pilih gambar baru terlebih dahulu", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isUploading = true
                        val jwtTokenRaw = PreferencesHelper.getJwtToken(context)
                        val jwtToken = "Bearer ${jwtTokenRaw ?: ""}"

                        val tempFile = gambarUri?.let { uri ->
                            try {
                                val contentResolver = context.contentResolver
                                val inputStream = contentResolver.openInputStream(uri)
                                val fileName = "uploaded_image_${System.currentTimeMillis()}.jpg"
                                val tempFile = File(context.cacheDir, fileName)
                                inputStream?.use { input ->
                                    FileOutputStream(tempFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                tempFile
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }
                        }

                        tempFile?.let { file ->
                            val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                            val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
                            RetrofitClient.apiService.uploadImage(jwtToken, filePart).enqueue(object : Callback<IPFSResponse> {
                                override fun onResponse(call: Call<IPFSResponse>, response: Response<IPFSResponse>) {
                                    isUploading = false
                                    if (response.isSuccessful) {
                                        response.body()?.let { ipfsResponse ->
                                            ipfsHash = ipfsResponse.cid
                                            Toast.makeText(context, "Gambar berhasil diupload!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Toast.makeText(context, "Gagal upload ke IPFS", Toast.LENGTH_SHORT).show()
                                    }
                                    file.delete()
                                }
                                override fun onFailure(call: Call<IPFSResponse>, t: Throwable) {
                                    isUploading = false
                                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                        } ?: run {
                            Toast.makeText(context, "Gagal membuat file dari URI", Toast.LENGTH_SHORT).show()
                            isUploading = false
                        }
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(if (isUploading) "Mengupload..." else "Konfirmasi Gambar Baru", color = Color.White)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (
                            namaTanaman.isBlank() || namaLatin.isBlank() || komposisi.isBlank() ||
                            kegunaan.isBlank() || dosis.isBlank() || caraPengolahan.isBlank() ||
                            efekSamping.isBlank() || ipfsHash.isBlank()
                        ) {
                            showError = true
                            Toast.makeText(context, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
                        } else {
                            showError = false
                            val jwtTokenRaw = PreferencesHelper.getJwtToken(context)
                            val jwtToken = jwtTokenRaw?.let {
                                if (it.startsWith("Bearer ")) it else "Bearer $it"
                            } ?: ""
                            val request = DataClassResponses.EditPlantRequest(
                                plantId = plantId,
                                name = namaTanaman,
                                namaLatin = namaLatin,
                                komposisi = komposisi,
                                kegunaan = kegunaan,
                                dosis = dosis,
                                caraPengolahan = caraPengolahan,
                                efekSamping = efekSamping,
                                ipfsHash = ipfsHash
                            )
                            viewModel.editPlant(
                                token = jwtToken,
                                request = request,
                                onSuccess = {
                                    Toast.makeText(context, "Tanaman berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Simpan Perubahan", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    isError: Boolean,
    onChange: (String) -> Unit
) {
    val maxChar = when (label) {
        "Cara Pengolahan" -> 1000
        "Kegunaan" -> 1000
        "Efek Samping" -> 1000
        "Komposisi" -> 750
        else -> 255
    }
    val isExceedLimit = value.length > maxChar
    val errorText = when {
        isError -> "$label tidak boleh kosong"
        isExceedLimit -> "$label tidak boleh lebih dari $maxChar karakter"
        else -> ""
    }
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Black)
        OutlinedTextField(
            value = value,
            onValueChange = { newText ->
                if (newText.length <= maxChar) onChange(newText)
            },
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            isError = isError || isExceedLimit,
            modifier = Modifier.fillMaxWidth()
        )
        if (errorText.isNotEmpty()) {
            Text(errorText, color = Color.Red, fontSize = MaterialTheme.typography.bodySmall.fontSize)
        }
    }
}
