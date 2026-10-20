package com.unand.asrama.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presensi_local")
data class PresensiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nim: String,
    val jenisPresensi: String,
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Double,
    val isMockLocation: Boolean,
    val isEmulator: Boolean,
    val wifiBssid: String?,
    val deviceDeviceId: String,
    val deviceTimestamp: String,
    val photoBase64: String,
    val isSynced: Boolean = false,
    val status: String = "Offline Pending",
    val createdAt: Long = System.currentTimeMillis()
)
