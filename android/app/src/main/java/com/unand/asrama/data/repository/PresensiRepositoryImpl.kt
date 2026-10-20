package com.unand.asrama.data.repository

import android.content.Context
import android.graphics.Bitmap
import androidx.work.*
import com.unand.asrama.data.local.AppDatabase
import com.unand.asrama.data.local.PreferenceManager
import com.unand.asrama.data.local.entity.PresensiEntity
import com.unand.asrama.data.remote.RetrofitClient
import com.unand.asrama.data.remote.dto.PresensiPayload
import com.unand.asrama.util.*
import com.unand.asrama.worker.PresensiSyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PresensiRepositoryImpl(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.presensiDao()
    private val pref = PreferenceManager(context)

    suspend fun submitAttendance(
        jenisPresensi: String,
        bitmap: Bitmap,
        bypassTimeGate: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        // 1. Emulator Detection Guardrail
        if (DeviceSecurityManager.isEmulator()) {
            return@withContext Result.failure(
                SecurityException("Deteksi Emulator: Presensi hanya diizinkan pada smartphone Android fisik!")
            )
        }

        // 2. Fetch High Accuracy Device Location
        val location = LocationHelper.getCurrentLocation(context)
            ?: return@withContext Result.failure(
                IllegalStateException("Tidak dapat memperoleh koordinat GPS. Pastikan GPS HP Anda aktif!")
            )

        // 3. Fake GPS / Mock Location Detection Guardrail
        val isMock = LocationHelper.isMockLocation(location)
        if (isMock) {
            return@withContext Result.failure(
                SecurityException("Deteksi Fake GPS: Terdeteksi Mock Location aktif pada perangkat Anda!")
            )
        }

        // 4. AI Liveness Verification (Google ML Kit)
        val liveness = FaceDetectionHelper.analyzeFaceLiveness(bitmap)
        if (!liveness.isLiveHuman) {
            return@withContext Result.failure(
                IllegalStateException("Verifikasi Wajah Gagal: ${liveness.message}")
            )
        }

        // 5. Client-Side Image Compression to Base64
        val photoBase64 = ImageUtils.compressBitmapToBase64(bitmap, maxDimension = 1080, quality = 75)
        val nim = pref.getNim() ?: "2211521001"
        val deviceId = pref.getDeviceId()
        val wifiBssid = WifiHelper.getConnectedBssid(context)
        val timestampIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault()).format(Date())

        val payload = PresensiPayload(
            nim = nim,
            jenisPresensi = jenisPresensi,
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracy.toDouble(),
            isMockLocation = false,
            isEmulator = false,
            wifiBssid = wifiBssid,
            deviceDeviceId = deviceId,
            deviceTimestamp = timestampIso,
            photoBase64 = photoBase64
        )

        // 6. Online Submission with Offline-First Fallback
        try {
            val response = RetrofitClient.apiService.submitPresensi(payload, bypassTimeGate)
            if (response.isSuccessful && response.body()?.success == true) {
                // Save locally marked as synced
                dao.insertPresensi(
                    PresensiEntity(
                        nim = nim,
                        jenisPresensi = jenisPresensi,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy.toDouble(),
                        isMockLocation = false,
                        isEmulator = false,
                        wifiBssid = wifiBssid,
                        deviceDeviceId = deviceId,
                        deviceTimestamp = timestampIso,
                        photoBase64 = photoBase64,
                        isSynced = true,
                        status = "Hadir (Online)"
                    )
                )
                Result.success(response.body()?.message ?: "Presensi berhasil dicatat!")
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(RuntimeException(errorMsg))
            }
        } catch (e: Exception) {
            // Network failure: Persist to encrypted local Room DB for offline background sync
            dao.insertPresensi(
                PresensiEntity(
                    nim = nim,
                    jenisPresensi = jenisPresensi,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracyMeters = location.accuracy.toDouble(),
                    isMockLocation = false,
                    isEmulator = false,
                    wifiBssid = wifiBssid,
                    deviceDeviceId = deviceId,
                    deviceTimestamp = timestampIso,
                    photoBase64 = photoBase64,
                    isSynced = false,
                    status = "Offline Pending (Tersimpan Lokal)"
                )
            )

            // Trigger immediate background sync request when network becomes available
            triggerImmediateOfflineSync()

            Result.success("Internet offline: Presensi disimpan di HP & akan disinkronkan otomatis oleh WorkManager saat jaringan stabil.")
        }
    }

    private fun triggerImmediateOfflineSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<PresensiSyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    fun getLocalHistory(): Flow<List<PresensiEntity>> = dao.getAllLocalHistory()
}
