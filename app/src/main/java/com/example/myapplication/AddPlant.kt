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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R

@Composable
fun AddPlant() {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    var namaTanaman by remember { mutableStateOf("") }
    var namaLatin by remember { mutableStateOf("") }
    var komposisi by remember { mutableStateOf("") }
    var manfaat by remember { mutableStateOf("") }
    var caraPengolahan by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }

    // Validasi state error
    var showError by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> gambarUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE6F1E9))
            .padding(16.dp)
            .verticalScroll(scrollState)
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
                FormField("Nama Tanaman", namaTanaman, showError && namaTanaman.isBlank()) { namaTanaman = it }
                FormField("Nama Latin", namaLatin, showError && namaLatin.isBlank()) { namaLatin = it }
                FormField("Komposisi", komposisi, showError && komposisi.isBlank()) { komposisi = it }
                FormField("Manfaat", manfaat, showError && manfaat.isBlank()) { manfaat = it }
                FormField("Cara Pengolahan", caraPengolahan, showError && caraPengolahan.isBlank()) { caraPengolahan = it }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Gambar Tanaman", fontSize = 14.sp, fontWeight = FontWeight.Medium)

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pilih Gambar", color = Color.Black)
                }

                gambarUri?.let { uri ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preview Gambar:", fontWeight = FontWeight.Medium)
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

                if (showError && gambarUri == null) {
                    Text(
                        text = "Gambar tidak boleh kosong",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (namaTanaman.isBlank() || namaLatin.isBlank() ||
                            komposisi.isBlank() || manfaat.isBlank() ||
                            caraPengolahan.isBlank() || gambarUri == null
                        ) {
                            showError = true
                        } else {
                            showError = false
                            Toast.makeText(context, "Tanaman berhasil disimpan", Toast.LENGTH_SHORT).show()
                            // TODO: Simpan data ke API atau database
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
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = false,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            textStyle = TextStyle(fontSize = 14.sp),
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
