package com.unand.asrama.data.remote

import com.unand.asrama.data.remote.dto.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponse<LoginResponseDto>>

    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: Map<String, String>): Response<ApiResponse<LoginResponseDto>>

    @POST("api/presensi")
    suspend fun submitPresensi(
        @Body payload: PresensiPayload,
        @Query("bypassTimeGate") bypassTimeGate: Boolean = false
    ): Response<ApiResponse<Any>>

    @GET("api/presensi/status-hari-ini/{userId}")
    suspend fun getStatusHariIni(@Path("userId") userId: Int): Response<ApiResponse<Map<String, Any>>>

    @GET("api/presensi/user/{userId}")
    suspend fun getPresensiUser(@Path("userId") userId: Int): Response<ApiResponse<List<Any>>>

    @POST("api/izin")
    suspend fun submitIzin(@Body request: IzinRequestDto): Response<ApiResponse<Any>>

    @GET("api/izin/user/{userId}")
    suspend fun getIzinUser(@Path("userId") userId: Int): Response<ApiResponse<List<Any>>>
}

object RetrofitClient {
    // 10.0.2.2 points to localhost from Android Emulator, or LAN IP for physical device
    var BASE_URL = "http://10.0.2.2:8080/"

    private var authToken: String? = null

    fun setToken(token: String?) {
        authToken = token
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            authToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }
            chain.proceed(requestBuilder.build())
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
