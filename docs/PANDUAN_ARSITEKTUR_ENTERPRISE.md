# Panduan Arsitektur & Spesifikasi Enterprise SIA-Asrama UNAND
**Sistem Informasi Absensi Penghuni Asrama Universitas Andalas (Enterprise Edition)**  
*Mata Kuliah: Pengembangan Aplikasi Berbasis Framework*

---

## 1. Arsitektur Teknologi & Clean Architecture

### A. Backend (Java Spring Boot 3.x)
- **Framework & Core**: Spring Boot 3.x, Spring Data JPA, Spring Web MVC.
- **Security & RBAC**: Spring Security dengan **JWT Access Token** (2 jam) & **Refresh Token** (7 hari), enkripsi password **BCrypt**.
- **Database & Storage**: MySQL (`asrama_unand_db`) dengan foreign keys, unique constraint `(user_id, tanggal, jenis_presensi)`.
- **Anti-Cheat & Geofencing Engine**:
  - Validasi jarak menggunakan **Formula Haversine** ($\Delta \text{distance} < 50 \text{ meter}$).
  - Dynamic Server Time-Gated System (NTP Asia/Jakarta) untuk Subuh (04.00-06.00 WIB) & Malam (18.00-20.30 WIB) dengan penanganan otomatis status "Terlambat".
  - Anti-Spoofing: Deteksi Fake GPS (`isMockLocation`) dan deteksi emulator Android.
  - **Single-Device Lock**: Mengunci akun penghuni ke satu IMEI / Android ID unik.
  - **Dynamic Watermarking & Geotagging**: Server-side image processing (`Graphics2D`) yang menempelkan banner permanen berisi Timestamp NTP, Lat/Long, Akurasi, dan Nama Gedung langsung ke berkas foto selfie.
- **Audit Trail**: Pencatatan riwayat koreksi presensi ke tabel `audit_log` (Fasil ID, Timestamp, Status Lama, Status Baru, IP Address, Alasan) yang bersifat *unalterable* (immutable).
- **Excel Batch & Report**: Apache POI untuk impor dan ekspor massal data mahasiswa (.xlsx) dan rekapitulasi presensi global.
- **Automated Scheduler**: Cron job otomatis yang menandai status `Alpha` bagi penghuni yang tidak hadir setelah batas waktu sesi ditutup.

### B. Mobile App (Android Kotlin Clean Architecture & MVVM)
- **Direktori**: `android/`
- **UI Framework**: Jetpack Compose dengan Material 3 Design System.
- **Pola Desain**: Clean Architecture (Data Layer, Domain Layer, Presentation / UI Layer) + MVVM.
- **Kamera & AI Biometrik**:
  - CameraX API untuk selfie capture langsung.
  - **Google ML Kit Face Detection**: Analisis liveness di perangkat (*on-device liveness detection*) untuk memvalidasi manusia nyata vs foto cetak/layar HP.
  - **BiometricPrompt API**: Otentikasi sidik jari / Face Unlock perangkat.
- **Location & Geofencing**: Google Play Services Location (`FusedLocationProviderClient`) dengan pengecekan `isMockLocation`.
- **Offline-First & Background Sync**:
  - Room Database lokal untuk menampung presensi offline saat jaringan asrama terputus.
  - **WorkManager**: Sinkronisasi latar belakang otomatis saat perangkat kembali terhubung ke internet.
- **Networking**: Retrofit 2 + Gson + OkHttp Interceptor untuk injeksi otomatis JWT Bearer token.

### C. Web Frontend (React.js + Vite)
- **Direktori**: `frontend/`
- **UI & UX**: Responsive Glassmorphism Dashboard dengan Lucide Icons.
- **Fitur Penghuni**: Live countdown timer, auto-lock button, interactive webcam capture, simulator anti-cheat, smart document uploader dengan kompresi client-side otomatis.
- **Fitur Fasilitator**: Live monitoring kehadiran penghuni binaan, approval modal dengan Lightbox Viewer untuk bukti izin, koreksi manual ber-audit.
- **Fitur Administrator**: Master CRUD pengguna, pemetaan gedung & alokasi kamar/lorong, ekspor/impor Excel massal, analitik eksekutif universitas.

---

## 2. Skema Payload REST API Presensi

Endpoint: `POST /api/presensi` (Mendukung query parameter opsional `bypassTimeGate=true` untuk kemudahan pengujian dosen/evaluator).

### JSON Request Payload
```json
{
  "nim": "2211521001",
  "jenisPresensi": "SUBUH",
  "latitude": -0.914231,
  "longitude": 100.466012,
  "accuracyMeters": 3.8,
  "isMockLocation": false,
  "isEmulator": false,
  "wifiBssid": "70:3a:0e:aa:bb:cc",
  "deviceDeviceId": "ANDROID_ID_A1B2C3D4",
  "deviceTimestamp": "2026-10-20T05:15:30+07:00",
  "photoBase64": "data:image/jpeg;base64,..."
}
```

### JSON Response (Success 200 OK)
```json
{
  "success": true,
  "message": "Presensi SUBUH berhasil dicatat! Status: Hadir",
  "data": {
    "id": 1,
    "tanggal": "2026-10-20",
    "waktuAbsen": "2026-10-20 05:15:30",
    "jenisPresensi": "SUBUH",
    "status": "Hadir",
    "latitude": -0.91423100,
    "longitude": 100.46601200,
    "distanceMeters": 4.2,
    "accuracyMeters": 3.8,
    "fotoWajah": "/uploads/presensi/presensi_2211521001_1760948130000.jpg",
    "keterangan": "SUBUH - Sukses"
  }
}
```

---

## 3. Kredensial Pengujian Akun Bawaan (Default Seed Data)

| Role | Identitas (NIM / NIP / Email) | Password | Perangkat Terikat (Device ID) | Keterangan |
|---|---|---|---|---|
| **Penghuni** | `2211521001` atau `penghuni@unand.ac.id` | `penghuni123` | `ANDROID_ID_A1B2C3D4` | Muhammad Farhan (Kamar A.101, Gedung Putra A) |
| **Penghuni** | `2211522002` atau `siti@unand.ac.id` | `penghuni123` | `ANDROID_ID_E5F6G7H8` | Siti Aminah (Kamar B.201, Gedung Putri B) |
| **Fasilitator** | `FAS001` atau `fasil@unand.ac.id` | `fasil123` | Bebas | Fasilitator Utama (Pembina Lorong 1) |
| **Administrator** | `ADM001` atau `admin@unand.ac.id` | `admin123` | Bebas | Administrator Pusat Asrama UNAND |

---

## 4. Cara Menjalankan Aplikasi

1. **Jalankan Database MySQL**:
   Nyalakan MySQL pada XAMPP atau Docker pada port default `3306`.
2. **Jalankan Otomatis via Batch Script**:
   Klik dua kali file `jalankan_aplikasi.bat` di root direktori. Script akan otomatis:
   - Menjalankan Backend Spring Boot pada port `8080`.
   - Menjalankan Frontend React Vite pada port `5173`.
3. **Buka Peramban (Browser)**:
   Akses `http://localhost:5173` untuk membuka antarmuka web SIA-Asrama UNAND.
4. **Project Android**:
   Buka folder `android/` pada Android Studio (Hedgehog/Iguana/Jellyfish), lakukan Gradle Sync, dan jalankan pada perangkat fisik atau emulator Android.
