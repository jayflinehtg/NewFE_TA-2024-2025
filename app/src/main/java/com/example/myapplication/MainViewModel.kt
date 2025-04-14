package com.example.myapplication

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.EventSink
import com.example.myapplication.data.PreferencesHelper
import com.example.myapplication.data.UiEvent
import com.example.myapplication.data.UiState
import com.example.myapplication.services.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.metamask.androidsdk.EthereumFlow
import io.metamask.androidsdk.EthereumMethod
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ethereum: EthereumFlow,
    @ApplicationContext private val context: Context,
    private val apiService: ApiService
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(
        UiState(
            shouldShowWalletConnect = !PreferencesHelper.isMetaMaskConnected(context)
        ).copy(fullName = PreferencesHelper.getUserFullName(context) ?: "")
    )
    val uiState = _uiState.asStateFlow()

    private fun showMessage(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Message(message))
        }
    }

    fun eventSink(eventSink: EventSink) {
        viewModelScope.launch {
            when (eventSink) {
                EventSink.Connect -> connectWallet()
                EventSink.GetBalance -> updateBalance()
                EventSink.Disconnect -> disconnectWallet()
                EventSink.GuestLogin -> handleGuestLogin()
            }
        }
    }

    fun fetchUserDataFromPrefs() {
        val fullName = PreferencesHelper.getUserFullName(context) ?: "Nama Pengguna"
        val isLoggedIn = PreferencesHelper.getJwtToken(context) != null
        _uiState.update { state ->
            state.copy(
                fullName = fullName,
                isLoggedIn = isLoggedIn
            )
        }
    }

    fun logout() {
        _uiState.update { it.copy(isConnecting = true, message = null) }
        viewModelScope.launch {
            try {
                val response = apiService.logoutUser().execute()
                if (response.isSuccessful) {
                    PreferencesHelper.clearJwtToken(context)
                    PreferencesHelper.saveMetaMaskConnectionStatus(context, false)
                    PreferencesHelper.clearWalletAddress(context)
                    PreferencesHelper.clearUserFullName(context)
                    _uiState.update {
                        it.copy(
                            walletAddress = null,
                            fullName = null,
                            isGuest = false,
                            message = "Berhasil keluar." // Set success message
                        )
                    }
                    _uiEvent.emit(UiEvent.NavigateTo("walletComponent"))
                    _uiEvent.emit(UiEvent.Message("Berhasil keluar."))
                } else {
                    Log.e("MainViewModel", "Logout failed: ${response.errorBody()?.string()}")
                    _uiState.update { it.copy(isConnecting = false, message = "Gagal keluar.") } // Set error message
                    _uiEvent.emit(UiEvent.Message("Gagal keluar."))
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Logout error: ${e.message}")
                _uiState.update { it.copy(isConnecting = false, message = "Terjadi kesalahan saat keluar.") } // Set error message
                _uiEvent.emit(UiEvent.Message("Terjadi kesalahan saat keluar."))
            } finally {
                _uiState.update { it.copy(isConnecting = false) }
            }
        }
    }

    private fun connectWallet() {
        viewModelScope.launch {
            when (val result = ethereum.connect()) {
                is Result.Success -> {
                    val address = ethereum.selectedAddress
                    if (address.isNotEmpty()) {
                        PreferencesHelper.saveMetaMaskConnectionStatus(context, true)
                        PreferencesHelper.saveWalletAddress(context, address)

                        _uiState.update {
                            it.copy(
                                walletAddress = address,
                                isConnecting = false,
                                shouldShowWalletConnect = false,
                                isGuest = false,
                                balance = "0.0 ETH"
                            )
                        }
                        _uiEvent.emit(UiEvent.NavigateTo("login"))
                        updateBalance()
                        fetchUserDataFromPrefs()
                    } else {
                        showMessage("Gagal mendapatkan alamat wallet")
                    }
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isConnecting = false) }
                    showMessage("Terjadi kesalahan: ${result.error.message}")
                }
            }
        }
    }

    private fun updateBalance() {
        if (ethereum.selectedAddress.isNotEmpty()) {
            viewModelScope.launch {
                val balanceResult = ethereum.sendRequest(
                    EthereumRequest(
                        method = EthereumMethod.ETH_GET_BALANCE.value,
                        params = listOf(ethereum.selectedAddress, "latest")
                    )
                )
                when (balanceResult) {
                    is Result.Success.Item -> {
                        val cleanHexString = if (balanceResult.value.startsWith("0x")) {
                            balanceResult.value.substring(2)
                        } else {
                            balanceResult.value
                        }
                        _uiState.update {
                            it.copy(balance = "${BigInteger(cleanHexString, 16)} ETH")
                        }
                    }
                    is Result.Error -> showMessage(balanceResult.error.message)
                    else -> _uiState.update { it.copy(balance = "NA") }
                }
            }
        } else {
            showMessage("Dompet belum terhubung!")
        }
    }

    private fun disconnectWallet() {
        _uiState.update {
            it.copy(
                walletAddress = null,
                isConnecting = false,
                balance = null,
                shouldShowWalletConnect = true,
                fullName = null
            )
        }
        ethereum.disconnect(true)
        PreferencesHelper.saveMetaMaskConnectionStatus(context, false)
        PreferencesHelper.clearJwtToken(context)
        PreferencesHelper.clearWalletAddress(context)
        PreferencesHelper.clearUserFullName(context)
        showMessage("Disconnected!")
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateTo("walletComponent"))
        }
    }

    private fun handleGuestLogin() {
        _uiState.update {
            it.copy(
                walletAddress = "",
                isConnecting = false,
                shouldShowWalletConnect = false,
                balance = "Guest",
                isGuest = true,
                fullName = "Tamu"
            )
        }
        PreferencesHelper.saveMetaMaskConnectionStatus(context, false)
        PreferencesHelper.saveUserRegistrationStatus(context, false)
        PreferencesHelper.saveUserFullName(context, "Tamu")
        showMessage("Masuk sebagai Tamu")
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateTo("home"))
        }
    }

    fun onRegisterSuccess(walletAddress: String) {
        try {
            PreferencesHelper.saveWalletAddress(context, walletAddress)
            fetchUserDataFromPrefs ()
            showMessage("Pendaftaran Berhasil!")
        } catch (e: Exception) {
            showMessage("Terjadi kesalahan saat pendaftaran: ${e.message}")
        }
    }

    init {
        initializeConnection()
        if (PreferencesHelper.isMetaMaskConnected(context) && !PreferencesHelper.getWalletAddress(context).isNullOrEmpty()) {
            fetchUserDataFromPrefs ()
        }
    }

    private fun initializeConnection() {
        viewModelScope.launch {
            val savedAddress = PreferencesHelper.getWalletAddress(context)
            if (PreferencesHelper.isMetaMaskConnected(context) && !savedAddress.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        walletAddress = savedAddress,
                        isConnecting = true
                    )
                }
                updateBalance()
                fetchUserDataFromPrefs ()
            }
        }
    }
}