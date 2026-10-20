package com.unand.asrama

import android.app.Application
import androidx.work.*
import com.unand.asrama.data.local.AppDatabase
import com.unand.asrama.worker.PresensiSyncWorker
import java.util.concurrent.TimeUnit

class SIAAsramaApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    override fun onCreate() {
        super.onCreate()
        setupPeriodicOfflineSync()
    }

    private fun setupPeriodicOfflineSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<PresensiSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "PresensiOfflineSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
}
