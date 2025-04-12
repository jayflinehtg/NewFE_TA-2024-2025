package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun Profile(navController: NavController, onLogout: () -> Unit) {

    // Data dummy untuk tampilan
    val fullName = "John Doe"
    val walletAddress = "0x1234567890abcdef"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Nama: $fullName", fontSize = 18.sp, modifier = Modifier.padding(8.dp))
        Text("Wallet Address: $walletAddress", fontSize = 18.sp, modifier = Modifier.padding(8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Logout untuk logout dan disconnect wallet
        Button(
            onClick = { onLogout() }, // Memanggil callback logout yang diberikan
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Logout", fontSize = 14.sp, color = Color.White)
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
