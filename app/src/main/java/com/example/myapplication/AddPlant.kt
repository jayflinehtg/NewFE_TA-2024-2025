package com.example.myapplication

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.data.DataClassResponses
import com.example.myapplication.data.IPFSResponse
import com.example.myapplication.data.PreferencesHelper
import com.example.myapplication.services.IPFSService
import com.example.myapplication.services.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

@Composable
fun AddPlant(
    viewModel: PlantViewModel = hiltViewModel(),
    ipfsService: IPFSService = RetrofitClient.ipfsService
) {
    val context = LocalContext.current
    var namaTanaman by remember { mutableStateOf("") }
    var namaLatin by remember { mutableStateOf("") }
    var komposisi by remember { mutableStateOf("") }
    var manfaat by remember { mutableStateOf("") }
    var caraPengolahan by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var showError by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var cid by remember { mutableStateOf("") }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> gambarUri = uri }

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
                    "Tambah Data Tanaman",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
                Text("Tanaman Herbal", fontSize = 16.sp, color = Color(0xFF4CAF50))
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
                // Form fields dengan teks hitam
                FormField("Nama Tanaman", namaTanaman, showError && namaTanaman.isBlank()) { namaTanaman = it }
                FormField("Nama Latin", namaLatin, showError && namaLatin.isBlank()) { namaLatin = it }
                FormField("Komposisi", komposisi, showError && komposisi.isBlank()) { komposisi = it }
                FormField("Manfaat", manfaat, showError && manfaat.isBlank()) { manfaat = it }
                FormField("Cara Pengolahan", caraPengolahan, showError && caraPengolahan.isBlank()) { caraPengolahan = it }

                Spacer(modifier = Modifier.height(12.dp))

                // Judul "Gambar Tanaman" dengan warna hitam
                Text("Gambar Tanaman", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)

                // Button untuk memilih gambar
                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pilih Gambar", color = Color.Black) // Warna teks pada button
                }

                // Menampilkan preview gambar jika ada
                gambarUri?.let { uri ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preview Gambar:", fontWeight = FontWeight.Medium, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Preview Gambar Tanaman",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(4.dp)
                    )
                }

                // Menampilkan error jika gambar tidak ada
                if (showError && gambarUri == null) {
                    Text(
                        text = "Gambar tidak boleh kosong",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Button Konfirmasi
                Button(
                    onClick = {
                        if (gambarUri == null) {
                            showError = true
                            return@Button
                        }

                        showError = false
                        isUploading = true

                        val jwtTokenRaw = PreferencesHelper.getJwtToken(context)
                        val jwtToken = "Bearer ${jwtTokenRaw ?: ""}"

                        val file = File(gambarUri?.path ?: "")
                        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

                        ipfsService.uploadImage(jwtToken, filePart).enqueue(object : Callback<IPFSResponse> {
                            override fun onResponse(call: Call<IPFSResponse>, response: Response<IPFSResponse>) {
                                isUploading = false
                                if (response.isSuccessful) {
                                    response.body()?.let { ipfsResponse ->
                                        cid = ipfsResponse.cid
                                        Toast.makeText(context, "CID: $cid", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Gagal upload ke IPFS", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<IPFSResponse>, t: Throwable) {
                                isUploading = false
                                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    },
                    enabled = !isUploading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isUploading) "Sedang Upload..." else "Konfirmasi",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Tombol simpan
                Button(
                    onClick = {
                        if (namaTanaman.isBlank() || namaLatin.isBlank() ||
                            komposisi.isBlank() || manfaat.isBlank() ||
                            caraPengolahan.isBlank() || cid.isBlank()
                        ) {
                            showError = true
                            Toast.makeText(context, "Mohon lengkapi semua data dan lakukan konfirmasi gambar", Toast.LENGTH_SHORT).show()
                        } else {
                            showError = false

                            val jwtTokenRaw = PreferencesHelper.getJwtToken(context)
                            val jwtToken = jwtTokenRaw?.let {
                                if (it.startsWith("Bearer ")) it else "Bearer $it"
                            } ?: ""

                            val request = DataClassResponses.AddPlantRequest(
                                name = namaTanaman,
                                namaLatin = namaLatin,
                                komposisi = komposisi,
                                kegunaan = manfaat,
                                caraPengolahan = caraPengolahan,
                                ipfsHash = cid
                            )

                            viewModel.addPlant(
                                token = jwtToken,
                                request = request,
                                onSuccess = {
                                    Toast.makeText(context, "Tanaman berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
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
                    Text("Simpan", fontSize = 16.sp, color = Color.White)
                }
            }

        }
    }
}

@Composable
fun FormField(label: String, value: String, isError: Boolean, onChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black // Warna label hitam
        )
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(color = Color.Black, fontSize = 14.sp), // Warna teks hitam
            isError = isError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            shape = RoundedCornerShape(12.dp)
        )
        if (isError) {
            Text(
                text = "$label tidak boleh kosong",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
