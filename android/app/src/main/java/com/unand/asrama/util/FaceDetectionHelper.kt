package com.unand.asrama.util

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FaceDetectionHelper {

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.25f)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(highAccuracyOpts)

    data class LivenessResult(
        val isFaceDetected: Boolean,
        val faceCount: Int,
        val isLiveHuman: Boolean,
        val eyeBlinkDetected: Boolean,
        val message: String
    )

    suspend fun analyzeFaceLiveness(bitmap: Bitmap): LivenessResult {
        val image = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { continuation ->
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        continuation.resume(
                            LivenessResult(
                                isFaceDetected = false,
                                faceCount = 0,
                                isLiveHuman = false,
                                eyeBlinkDetected = false,
                                message = "Wajah tidak terdeteksi! Pastikan wajah Anda terlihat jelas di dalam frame."
                            )
                        )
                    } else if (faces.size > 1) {
                        continuation.resume(
                            LivenessResult(
                                isFaceDetected = true,
                                faceCount = faces.size,
                                isLiveHuman = false,
                                eyeBlinkDetected = false,
                                message = "Terdeteksi lebih dari satu wajah (${faces.size} orang). Presensi hanya untuk satu penghuni!"
                            )
                        )
                    } else {
                        val face = faces[0]
                        val leftEyeOpen = face.leftEyeOpenProbability ?: -1f
                        val rightEyeOpen = face.rightEyeOpenProbability ?: -1f

                        // Liveness criteria: Natural eye open probability check (detects live human vs flat printed photo)
                        val naturalFace = (leftEyeOpen > 0.2f || rightEyeOpen > 0.2f)
                        val isBlinking = (leftEyeOpen < 0.35f || rightEyeOpen < 0.35f)

                        continuation.resume(
                            LivenessResult(
                                isFaceDetected = true,
                                faceCount = 1,
                                isLiveHuman = naturalFace,
                                eyeBlinkDetected = isBlinking,
                                message = if (naturalFace) "Verifikasi Wajah & Liveness Berhasil!" else "Liveness gagal! Harap lakukan kedipan mata alami."
                            )
                        )
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resume(
                        LivenessResult(
                            isFaceDetected = false,
                            faceCount = 0,
                            isLiveHuman = false,
                            eyeBlinkDetected = false,
                            message = "Gagal memproses deteksi wajah: ${e.localizedMessage}"
                        )
                    )
                }
        }
    }
}
