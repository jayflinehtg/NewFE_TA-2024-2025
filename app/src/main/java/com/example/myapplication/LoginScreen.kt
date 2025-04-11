package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.metamask.androidsdk.Result

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, walletAddress: String?, onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var isWalletConnected by remember { mutableStateOf(walletAddress != null) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.soft_green)), // Soft green background
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            // ==== LOGO ====
            Image(
                painter = painterResource(id = R.drawable.plant), // Replace with R.drawable.plant
                contentDescription = "Logo Tanaman",
                modifier = Modifier
                    .size(130.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                text = "LOGIN",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.dark_green),
            )

            Text(
                text = "Silakan Bergabung!",
                fontSize = 14.sp,
                color = colorResource(id = R.color.black)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.green)),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.85f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Kata Sandi") },
                        placeholder = { Text("Masukkan Kata Sandi") },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(fontSize = 16.sp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        isError = passwordError != null,
                        colors = TextFieldDefaults.colors( // Menggunakan TextFieldDefaults.colors
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            errorContainerColor = Color.White
                        )
                    )
                    if (passwordError != null) {
                        Text(
                            passwordError!!,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Login Button
                    Button(
                        onClick = { /* Handle login */ },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_green)), // Warna hijau gelap
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Text("Masuk", fontSize = 14.sp, color = Color.White)
                    }

                    // Link ke Register
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            "Belum memiliki akun? Daftar disini",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.purple) // Purple
                        )
                    }
                }
            }
        }
    }
}