package com.example.myapplication.data

data class UiState(
    val isConnecting: Boolean = false,
    val balance: String? = null,
    val walletAddress: String? = null,
    val shouldShowWalletConnect: Boolean = true,
    val isGuest: Boolean = false
)