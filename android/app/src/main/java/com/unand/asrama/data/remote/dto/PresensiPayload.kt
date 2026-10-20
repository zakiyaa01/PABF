package com.unand.asrama.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Enterprise Presensi Payload matching Spring Boot REST Controller schema
 */
data class PresensiPayload(
    @SerializedName("nim")
    val nim: String,

    @SerializedName("jenisPresensi")
    val jenisPresensi: String, // "SUBUH" or "MALAM"

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("accuracyMeters")
    val accuracyMeters: Double,

    @SerializedName("isMockLocation")
    val isMockLocation: Boolean,

    @SerializedName("isEmulator")
    val isEmulator: Boolean,

    @SerializedName("wifiBssid")
    val wifiBssid: String?,

    @SerializedName("deviceDeviceId")
    val deviceDeviceId: String,

    @SerializedName("deviceTimestamp")
    val deviceTimestamp: String,

    @SerializedName("photoBase64")
    val photoBase64: String
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T?
)

data class LoginRequestDto(
    @SerializedName("identifier")
    val identifier: String,

    @SerializedName("password")
    val password: String?,

    @SerializedName("deviceId")
    val deviceId: String?,

    @SerializedName("isBiometric")
    val isBiometric: Boolean = false
)

data class LoginResponseDto(
    @SerializedName("token")
    val token: String?,

    @SerializedName("refreshToken")
    val refreshToken: String?,

    @SerializedName("id")
    val id: Int,

    @SerializedName("nim")
    val nim: String?,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("namaGedung")
    val namaGedung: String?,

    @SerializedName("noKamar")
    val noKamar: String?,

    @SerializedName("lorong")
    val lorong: String?,

    @SerializedName("fasilNama")
    val fasilNama: String?,

    @SerializedName("deviceId")
    val deviceId: String?,

    @SerializedName("biometricEnabled")
    val biometricEnabled: Boolean?
)

data class IzinRequestDto(
    @SerializedName("nim")
    val nim: String,

    @SerializedName("tglMulai")
    val tglMulai: String,

    @SerializedName("tglSelesai")
    val tglSelesai: String,

    @SerializedName("kategori")
    val kategori: String,

    @SerializedName("alasan")
    val alasan: String,

    @SerializedName("fileBuktiBase64")
    val fileBuktiBase64: String?
)
