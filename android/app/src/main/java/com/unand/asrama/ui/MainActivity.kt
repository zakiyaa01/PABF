package com.unand.asrama.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.unand.asrama.data.local.PreferenceManager
import com.unand.asrama.data.remote.RetrofitClient
import com.unand.asrama.data.remote.dto.IzinRequestDto
import com.unand.asrama.data.remote.dto.LoginRequestDto
import com.unand.asrama.data.repository.PresensiRepositoryImpl
import com.unand.asrama.ui.screens.*
import com.unand.asrama.ui.theme.SIAAsramaTheme
import com.unand.asrama.util.BiometricAuthManager
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {

    private lateinit var pref: PreferenceManager
    private lateinit var presensiRepository: PresensiRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref = PreferenceManager(this)
        presensiRepository = PresensiRepositoryImpl(this)

        RetrofitClient.setToken(pref.getToken())

        setContent {
            SIAAsramaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember {
                        mutableStateOf(if (pref.getToken() != null) "DASHBOARD" else "LOGIN")
                    }

                    var activeJenisPresensi by remember { mutableStateOf("SUBUH") }
                    var isLoading by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf<String?>(null) }
                    var cameraStatusMessage by remember { mutableStateOf<String?>(null) }

                    val nama = remember { pref.getNama() ?: "Penghuni" }
                    val nim = remember { pref.getNim() ?: "2211521001" }

                    when (currentScreen) {
                        "LOGIN" -> {
                            LoginScreen(
                                onLoginClick = { id, pass ->
                                    isLoading = true
                                    errorMessage = null
                                    lifecycleScope.launch {
                                        try {
                                            val response = RetrofitClient.apiService.login(
                                                LoginRequestDto(
                                                    identifier = id,
                                                    password = pass,
                                                    deviceId = pref.getDeviceId(),
                                                    isBiometric = false
                                                )
                                            )
                                            if (response.isSuccessful && response.body()?.success == true) {
                                                val data = response.body()!!.data!!
                                                pref.saveAuthData(
                                                    token = data.token ?: "",
                                                    refreshToken = data.refreshToken,
                                                    userId = data.id,
                                                    nim = data.nim,
                                                    nama = data.nama,
                                                    role = data.role
                                                )
                                                RetrofitClient.setToken(data.token)
                                                currentScreen = "DASHBOARD"
                                            } else {
                                                errorMessage = response.errorBody()?.string() ?: "Login gagal!"
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Koneksi backend gagal: ${e.localizedMessage}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                onBiometricClick = {
                                    if (BiometricAuthManager.isBiometricAvailable(this@MainActivity)) {
                                        BiometricAuthManager.promptBiometricLogin(
                                            activity = this@MainActivity,
                                            onSuccess = {
                                                // Quick biometric login with device ID
                                                lifecycleScope.launch {
                                                    try {
                                                        val response = RetrofitClient.apiService.login(
                                                            LoginRequestDto(
                                                                identifier = pref.getNim() ?: "2211521001",
                                                                password = null,
                                                                deviceId = pref.getDeviceId(),
                                                                isBiometric = true
                                                            )
                                                        )
                                                        if (response.isSuccessful && response.body()?.success == true) {
                                                            currentScreen = "DASHBOARD"
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(this@MainActivity, "Biometrik login gagal", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            onError = { err ->
                                                Toast.makeText(this@MainActivity, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        Toast.makeText(this@MainActivity, "Sensor biometrik tidak tersedia", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                deviceId = pref.getDeviceId(),
                                isLoading = isLoading,
                                errorMessage = errorMessage
                            )
                        }

                        "DASHBOARD" -> {
                            DashboardScreen(
                                nama = nama,
                                nim = nim,
                                gedung = "Gedung Asrama Putra A",
                                kamar = "A.101",
                                fasilNama = "Fasilitator Utama (FAS001)",
                                onStartPresensi = { jenis ->
                                    activeJenisPresensi = jenis
                                    cameraStatusMessage = null
                                    currentScreen = "CAMERA"
                                },
                                onOpenIzin = {
                                    currentScreen = "IZIN"
                                },
                                onLogout = {
                                    pref.clear()
                                    RetrofitClient.setToken(null)
                                    currentScreen = "LOGIN"
                                }
                            )
                        }

                        "CAMERA" -> {
                            CameraPresensiScreen(
                                jenisPresensi = activeJenisPresensi,
                                onPhotoCaptured = { bitmap ->
                                    isLoading = true
                                    cameraStatusMessage = "Memproses deteksi liveness ML Kit & validasi geofence..."
                                    lifecycleScope.launch {
                                        val result = presensiRepository.submitAttendance(
                                            jenisPresensi = activeJenisPresensi,
                                            bitmap = bitmap,
                                            bypassTimeGate = true // Enables demo flexibility
                                        )
                                        isLoading = false
                                        result.fold(
                                            onSuccess = { msg ->
                                                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                                                currentScreen = "DASHBOARD"
                                            },
                                            onFailure = { err ->
                                                cameraStatusMessage = err.localizedMessage
                                            }
                                        )
                                    }
                                },
                                onBackClick = { currentScreen = "DASHBOARD" },
                                isProcessing = isLoading,
                                statusMessage = cameraStatusMessage
                            )
                        }

                        "IZIN" -> {
                            IzinFormScreen(
                                onBackClick = { currentScreen = "DASHBOARD" },
                                onSubmitIzin = { tglMulai, tglSelesai, kategori, alasan, buktiBase64 ->
                                    isLoading = true
                                    lifecycleScope.launch {
                                        try {
                                            val response = RetrofitClient.apiService.submitIzin(
                                                IzinRequestDto(
                                                    nim = nim,
                                                    tglMulai = tglMulai,
                                                    tglSelesai = tglSelesai,
                                                    kategori = kategori,
                                                    alasan = alasan,
                                                    fileBuktiBase64 = buktiBase64
                                                )
                                            )
                                            if (response.isSuccessful && response.body()?.success == true) {
                                                Toast.makeText(this@MainActivity, "Pengajuan izin berhasil terkirim!", Toast.LENGTH_SHORT).show()
                                                currentScreen = "DASHBOARD"
                                            } else {
                                                Toast.makeText(this@MainActivity, "Gagal mengirim izin", Toast.LENGTH_SHORT).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(this@MainActivity, "Koneksi gagal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                },
                                isSubmitting = isLoading
                            )
                        }
                    }
                }
            }
        }
    }
}
