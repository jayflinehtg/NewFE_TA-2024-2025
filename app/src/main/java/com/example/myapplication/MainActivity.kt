package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    object Detail : Screen("detail/{plantId}")
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

                // Handle UI events dari ViewModel (navigasi & pesan)
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is UiEvent.NavigateTo -> {
                                navController.navigate(event.route) {
                                    popUpTo("walletComponent") { inclusive = true }
                                }
                            }
                            is UiEvent.Message -> {
                                scope.launch {
                                    snackbarHostState.showSnackbar(event.message)
                                }
                            }
                        }
                    }
                }

                // Snackbar Host untuk menampilkan pesan error/sukses
                SnackbarHost(hostState = snackbarHostState)

                NavHost(
                    navController = navController,
                    startDestination = Screen.WalletComponent.route // Mulai dari WalletComponent
                ) {
                    // Wallet Component Screen
                    composable(Screen.WalletComponent.route) {
                        WalletComponent(
                            isConnecting = state.isConnecting,
                            balance = state.balance,
                            isGuest = state.isGuest,
                            eventSink = { event -> viewModel.eventSink(event) }
                        )
                    }

                    // Register Screen
                    composable(Screen.Register.route) {
                        RegisterScreen(
                            navController = navController,
                            onRegisterSuccess = { walletAddress ->
                                viewModel.onRegisterSuccess(walletAddress)
                                // Navigasi ke LoginScreen setelah berhasil registrasi
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Register.route) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(Screen.Register.route) { inclusive = false }
                                }
                            }
                        )
                    }

                    // Login Screen
                    composable(Screen.Login.route) {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route) {
                                    popUpTo(Screen.Login.route) { inclusive = false }
                                }
                            }
                        )
                    }

                    // Home Screen (Bottom Navigation)
                    composable(Screen.Home.route) {
                        BottomNavBarScreen(navController)
                    }

                    // Detail Screen (Dynamic PlantResponse ID)
                    composable(
                        Screen.Detail.route,
                        arguments = listOf(navArgument("plantId") { type = NavType.StringType })
                    ) { backStackEntry ->

                        val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
                        val plantViewModel: PlantViewModel = hiltViewModel()
                        val token = viewModel.userToken // Tambahkan ini di MainViewModel-mu jika belum

                        if (plantId.isNotEmpty()) {
                            DetailScreen(
                                plantId = plantId,
                                token = token,
                                viewModel = plantViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        } else {
                            Text("PlantResponse ID tidak valid.")
                        }
                    }
                }
            }
        }
    }
}
