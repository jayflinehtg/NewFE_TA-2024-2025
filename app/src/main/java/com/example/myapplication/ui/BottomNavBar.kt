package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.MainViewModel
import com.example.myapplication.ui.screens.*

@Composable
fun BottomNavBar(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("home")
                    },
                    label = { Text("Home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                BottomNavigationItem(
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("addPlant")
                    },
                    label = { Text("Tambah") },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Tambah") }
                )
                BottomNavigationItem(
                    selected = selectedItem == 2,
                    onClick = {
                        selectedItem = 2
                        navController.navigate("profile")
                    },
                    label = { Text("Profile") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "walletComponent",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { Home() }
            composable("addPlant") { AddPlant() }
            composable("profile") { Profile() }
            composable("walletComponent") {
                val viewModel: MainViewModel = hiltViewModel()
                val state by viewModel.uiState.collectAsState()

                WalletComponent(
                    isConnecting = state.isConnecting,
                    balance = state.balance,
                    eventSink = { event -> viewModel.eventSink(event) }
                )
            }
        }
    }
}
