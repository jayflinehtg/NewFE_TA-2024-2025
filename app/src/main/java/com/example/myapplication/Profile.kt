package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Profile(navController: NavController, onLogout: () -> Unit) {

    // Data dummy untuk tampilan
    val fullName = "Nama Pengguna"
    val email = "pengguna@example.com"
    val walletAddress = "0x1234567890abcdef"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.soft_green))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.plant), // Replace with R.drawable.plant
            contentDescription = "Logo Tanaman",
            modifier = Modifier
                .size(130.dp)
                .padding(bottom = 14.dp)
        )
        Text(
            text = fullName,
            fontSize = 28.sp, // Ukuran font "Nama Pengguna"
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.dark_green), // Warna hijau untuk "Nama Pengguna"
            modifier = Modifier.padding(bottom = 22.dp)
        )
        Text(
            text = "Public Key",
            fontSize = 14.sp, // Ukuran font "Public Key"
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = email,
            fontSize = 16.sp, // Ukuran font email
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Tombol Logout untuk logout dan disconnect wallet
        Button(
            onClick = { onLogout() }, // Memanggil callback logout yang diberikan
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(0.6f)
        ) {
            Text("Keluar", fontSize = 14.sp, color = Color.White)
        }
    }
}

@Composable
fun Profile() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile Screen")
        // Tambahkan konten profil Anda di sini
    }
}
