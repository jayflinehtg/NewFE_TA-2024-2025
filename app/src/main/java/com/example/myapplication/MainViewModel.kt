package com.example.myapplication

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.EventSink
import com.example.myapplication.data.PreferencesHelper
import com.example.myapplication.data.UiEvent
import com.example.myapplication.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.metamask.androidsdk.EthereumFlow
import io.metamask.androidsdk.EthereumMethod
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Result
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigInteger
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(
    private val ethereum: EthereumFlow,
    @ApplicationContext private val context: Context // Inject context untuk preferences helper
) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // Event untuk komunikasi satu kali, misalnya pesan error atau sukses
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    // State untuk UI, misalnya status koneksi MetaMask dan saldo wallet
    private val _uiState = MutableStateFlow(
        UiState(
            shouldShowWalletConnect = !PreferencesHelper.isMetaMaskConnected(context)
        )
    )
    val uiState = _uiState.asStateFlow()

    // Fungsi untuk menampilkan pesan ke user melalui UiEvent
    private fun showMessage(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.Message(message))
        }
    }

    // Sink event dari UI ke ViewModel untuk diproses lebih lanjut
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

    // Fungsi untuk menangani koneksi MetaMask wallet
    private fun connectWallet() {
        viewModelScope.launch {
            when (val result = ethereum.connect()) {
                is Result.Success -> {
                    val address = ethereum.selectedAddress
                    if (address.isNotEmpty()) {
                        PreferencesHelper.saveMetaMaskConnectionStatus(context, true)
                        PreferencesHelper.saveWalletAddress(context, address)

                        // Pastikan walletAddress diupdate sebelum navigasi
                        _uiState.update {
                            it.copy(
                                walletAddress = address,
                                isConnecting = false, // ✅ Matikan loading setelah berhasil
                                shouldShowWalletConnect = false,
                                isGuest = false,
                                balance = "0.0 ETH"
                            )
                        }

                        // Navigasi hanya jika address valid
                        _uiEvent.emit(UiEvent.NavigateTo("login"))
                        updateBalance()
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

    // Fungsi untuk mendapatkan saldo wallet dari MetaMask SDK
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

    // Fungsi untuk menangani logout atau disconnect dari MetaMask wallet
    private fun disconnectWallet() {
        _uiState.update {
            it.copy(
                walletAddress = null, // ✅ Reset walletAddress
                isConnecting = false,
                balance = null,
                shouldShowWalletConnect = true
            )
        }
        ethereum.disconnect(true)
        PreferencesHelper.saveMetaMaskConnectionStatus(context, false)
        showMessage("Disconnected!")
    }

    // Fungsi untuk login sebagai guest (tanpa koneksi MetaMask)
    private fun handleGuestLogin() {
        _uiState.update {
            it.copy(
                walletAddress = "", // ✅ Pastikan walletAddress kosong untuk guest
                isConnecting = false,
                shouldShowWalletConnect = false,
                balance = "Guest",
                isGuest = true
            )
        }
        PreferencesHelper.saveMetaMaskConnectionStatus(context, false)
        PreferencesHelper.saveUserRegistrationStatus(context, false)
        showMessage("Masuk sebagai Tamu")
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.NavigateTo("home"))
        }
    }

    fun onRegisterSuccess(walletAddress: String) {
        try {
            PreferencesHelper.saveWalletAddress(context, walletAddress)
            showMessage("Pendaftaran Berhasil!")
        } catch (e: Exception) {
            showMessage("Terjadi kesalahan saat pendaftaran: ${e.message}")
        }
    }

    // Inisialisasi koneksi MetaMask jika sebelumnya sudah terkoneksi
    init {
        initializeConnection()
    }

    // Inisialisasi Koneksi
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
            }
        }
    }
}
