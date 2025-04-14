package com.example.myapplication

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.DataClassResponses.LoginResponse
import com.example.myapplication.data.LoginRequest
import com.example.myapplication.data.PreferencesHelper
import com.example.myapplication.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val walletAddress = PreferencesHelper.getWalletAddress(context)

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Jika tidak ada walletAddress, navigasikan kembali ke WalletComponent
    LaunchedEffect(walletAddress) {
        if (walletAddress.isNullOrEmpty()) {
            navController.navigate("walletComponent") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.soft_green)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
        ) {
            // LOGO
            Image(
                painter = painterResource(id = R.drawable.plant), // Ganti dengan resource logo yang sesuai
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
                        colors = TextFieldDefaults.colors(
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
                        onClick = {
                            // Pastikan walletAddress tidak null
                            if (walletAddress != null) {
                                // Buat objek UserLogin untuk mengirim login request
                                val userLogin = LoginRequest(walletAddress, password)

                                // Panggil API untuk login
                                RetrofitClient.apiService.loginUser(userLogin).enqueue(object : Callback<LoginResponse> {
                                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                        if (response.isSuccessful) {
                                            Log.d("API", "Login Success: ${response.body()?.message}")
                                            // Menyimpan token JWT ke PreferencesHelper atau penyimpanan lokal
                                            val token = response.body()?.token ?: ""
                                            PreferencesHelper.saveJwtToken(context, token)

                                            // Menavigasi ke Home screen setelah berhasil login
                                            onLoginSuccess()
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            Log.d("API", "Login Failed: ${response.errorBody()?.string()}")
                                        }
                                    }

                                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                        Log.d("API", "Error: ${t.message}")
                                    }
                                })
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_green)),
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
                            color = colorResource(id = R.color.purple)
                        )
                    }
                }
            }
        }
    }
}
