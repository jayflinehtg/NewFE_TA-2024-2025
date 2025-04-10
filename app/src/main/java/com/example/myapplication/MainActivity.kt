package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.UiEvent
import com.example.myapplication.ui.components.BottomNavBar
import com.example.myapplication.ui.components.WalletComponent
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()

                // Navigasi otomatis saat wallet terhubung
                LaunchedEffect(state.isConnecting) {
                    if (state.isConnecting) {
                        navController.navigate("register") {
                            popUpTo("walletComponent") { inclusive = true }
                        }
                    }
                }

                // Gunakan pendekatan LaunchedEffect untuk menangani events
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.Message -> {
                                // Implementasi tampilkan toast atau snackbar
                                // Misalnya: Toast.makeText(this@MainActivity, event.error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "walletComponent"
                ) {
                    composable("walletComponent") {
                        WalletComponent(
                            isConnecting = state.isConnecting,
                            balance = state.balance,
                            eventSink = { event -> viewModel.eventSink(event) }
                        )
                    }
                    composable("register") {
                        RegisterScreen(navController)
                    }
                    composable("home") {
                        BottomNavBar(navController)
                    }
                }
            }
        }
    }
}
