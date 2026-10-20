package com.unand.asrama.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unand.asrama.data.local.AppDatabase
import com.unand.asrama.data.remote.RetrofitClient
import com.unand.asrama.data.remote.dto.PresensiPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PresensiSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = AppDatabase.getDatabase(applicationContext)
        val dao = database.presensiDao()
        val unsyncedList = dao.getUnsyncedPresensi()

        if (unsyncedList.isEmpty()) {
            return@withContext Result.success()
        }

        var allSuccessful = true

        for (record in unsyncedList) {
            try {
                val payload = PresensiPayload(
                    nim = record.nim,
                    jenisPresensi = record.jenisPresensi,
                    latitude = record.latitude,
                    longitude = record.longitude,
                    accuracyMeters = record.accuracyMeters,
                    isMockLocation = record.isMockLocation,
                    isEmulator = record.isEmulator,
                    wifiBssid = record.wifiBssid,
                    deviceDeviceId = record.deviceDeviceId,
                    deviceTimestamp = record.deviceTimestamp,
                    photoBase64 = record.photoBase64
                )

                val response = RetrofitClient.apiService.submitPresensi(payload)
                if (response.isSuccessful && response.body()?.success == true) {
                    dao.markAsSynced(record.id)
                } else {
                    allSuccessful = false
                }
            } catch (e: Exception) {
                allSuccessful = false
            }
        }

        if (allSuccessful) {
            Result.success()
        } else {
            Result.retry()
        }
    }
}
