package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.UiEvent
import com.example.myapplication.ui.components.BottomNavBarScreen
import com.example.myapplication.ui.components.WalletComponent
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object WalletComponent : Screen("walletComponent")
    object Register : Screen("register")
    object Login : Screen("login")
    object Home : Screen("home")
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val context = LocalContext.current

                SnackbarHost(hostState = snackbarHostState)

                // Navigasi otomatis ke register jika wallet terhubung & bukan guest
                LaunchedEffect(state.walletAddress, state.isGuest) {
                    if (state.walletAddress != null && !state.isGuest) {
                        navController.navigate("register") {
                            popUpTo("walletComponent") { inclusive = true }
                        }
                    }
                }

                // Handle UI events dari ViewModel
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.NavigateTo -> {
                                navController.navigate(event.route) {
                                    popUpTo("walletComponent") { inclusive = true }
                                }
                            }
                            else -> {}
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
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            navController = navController,
                            onRegisterSuccess = { walletAddress ->
                                viewModel.onRegisterSuccess(walletAddress)
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            walletAddress = state.walletAddress,
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register") {
                                    popUpTo("login") { inclusive = false }
                                }
                            }
                        )
                    }

                    composable("home") {
                        BottomNavBarScreen(navController)
                    }
                }
            }
        }
    }
}
