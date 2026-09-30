# Dokumen Perancangan Arsitektur Sistem Informasi Absensi Penghuni Asrama UNAND

## 1. Gambaran Umum Sistem

**Sistem Informasi Absensi Penghuni Asrama Universitas Andalas (UNAND)** adalah aplikasi web full-stack terintegrasi yang dirancang untuk mengelola dan memantau kehadiran penghuni asrama secara presisi, real-time, dan terkonfirmasi. Sistem ini mencakup 2 sesi presensi harian (Subuh & Malam), mekanisme pengajuan dan verifikasi izin beserta bukti dokumen (JPG/PNG), fitur koreksi presensi ber-audit, serta manajemen master data penghuni, fasil (fasilitator), gedung, dan kamar asrama.

---

## 2. Daftar Fitur Berdasarkan Role

### A. Role Penghuni (Student / Resident)
1. **Autentikasi**: Login & Logout dengan keamanan BCrypt.
2. **Dashboard Penghuni**:
   - Sapaan personal & info ringkas (NIM, Kamar, Gedung, Fasil Pembina).
   - Status presensi Subuh & Malam hari ini (Sudah / Belum / Berlangsung / Selesai).
   - Ringkasan statistik kehadiran bulan berjalan (Hadir, Izin, Alpha).
   - Akses cepat ke Formulir Presensi & Pengajuan Izin.
3. **Fitur Presensi (Subuh & Malam)**:
   - **Presensi Subuh**: Aktif pukul 04.00 – 06.00 WIB.
   - **Presensi Malam**: Aktif pukul 18.00 – 20.30 WIB.
   - Validasi jadwal & pencegahan presensi ganda di tingkat server & database constraint.
4. **Pengajuan Izin**:
   - Formulir pengajuan (Tanggal Mulai, Tanggal Selesai, Jenis Izin, Alasan).
   - Unggah bukti izin (Format JPG/PNG, Maksimal 2MB, MIME-type validation).
   - Monitoring status pengajuan (Menunggu / Disetujui / Ditolak) beserta catatan Fasil.
5. **Riwayat Presensi & Filter**:
   - Tabel/Kartu riwayat kehadiran personal.
   - Filter berdasarkan rentang tanggal, jenis presensi (Subuh/Malam), dan status.
   - Pagination untuk navigasi data yang nyaman.
6. **Profil Pribadi**:
   - Melihat data diri, akun, gedung, nomor kamar, dan Fasil yang mendampingi.

### B. Role Fasil (Fasilitator Asrama)
1. **Autentikasi**: Login & Logout.
2. **Dashboard Fasil**:
   - Ringkasan total penghuni dalam binaan.
   - Rekap persentase kehadiran Subuh & Malam hari ini.
   - Alert/Daftar penghuni yang belum melakukan presensi.
   - Notifikasi pengajuan izin yang membutuhkan verifikasi (*Pending*).
3. **Pemantauan & Rekap Presensi**:
   - Melihat presensi seluruh penghuni di bawah tanggung jawabnya.
   - Filter presensi berdasarkan tanggal, nama penghuni, jenis presensi, dan status.
   - Export / Rekapitulasi kehadiran per periode (Mingguan/Bulanan).
4. **Verifikasi & Keputusan Izin**:
   - Daftar pengajuan izin penghuni binaan.
   - Modal detail izin + *preview* bukti gambar JPG/PNG.
   - Aksi **Setujui** atau **Tolak** dengan input wajib *Catatan Keputusan*.
5. **Koreksi Presensi Ber-Audit**:
   - Fasilitas pengoreksian status presensi jika terjadi kesalahan teknis / kendala server.
   - Kewajiban mengisikan *Alasan Koreksi*.
   - Pencatatan otomatis ke `presensi_audit_log` (siapa yang mengubah, nilai lama, nilai baru, alasan, waktu).

### C. Role Admin (Administrator Asrama)
1. **Autentikasi**: Login & Logout.
2. **Dashboard Admin**:
   - Visualisasi statistik global (Total Penghuni, Total Fasil, Total Gedung, Persentase Kehadiran Total).
3. **Manajemen Master Data Pengguna**:
   - CRUD (Create, Read, Update, Deactivate) data Penghuni, Fasil, dan Admin.
   - Pengaturan status akun (Aktif / Nonaktif) tanpa merusak data historis presensi.
4. **Manajemen Gedung & Kamar**:
   - CRUD Gedung Asrama (Nama Gedung, Lokasi/Blok).
   - CRUD Kamar Asrama (Nomor Kamar, Lantai, Kapasitas, Gedung).
5. **Penugasan Fasil (Fasil Assignment)**:
   - Mengatur pemetaan Fasil pendamping untuk setiap Penghuni atau Kamar/Gedung.
6. **Rekapitulasi Global & Audit Monitoring**:
   - Melihat seluruh presensi penghuni se-asrama UNAND.
   - Memantau log audit perubahan presensi yang dilakukan Fasil/Admin.

---

## 3. Arsitektur Aplikasi (React.js + Spring Boot + MySQL)

Sistem menggunakan arsitektur **Decoupled Client-Server Architecture** (Frontend & Backend Terpisah):

```
+-----------------------------------------------------------------------+
|                           CLIENT SIDE (FRONTEND)                      |
|  React.js (Vite) + React Router + Axios + Modular CSS / Lucide Icons  |
+-----------------------------------------------------------------------+
                                   |
                         HTTP REST API (JSON)
                         CORS Configured
                                   v
+-----------------------------------------------------------------------+
|                          SERVER SIDE (BACKEND)                        |
|   Spring Boot Web + Spring Security + BCrypt + Spring Data JPA        |
|  (Controller -> Service -> Repository -> Security & Validation)       |
+-----------------------------------------------------------------------+
                                   |
                            JDBC Driver Connection
                                   v
+-----------------------------------------------------------------------+
|                         DATABASE SIDE (STORAGE)                       |
|           MySQL Server (Tables, Indexes, Foreign Keys, Constraints)    |
+-----------------------------------------------------------------------+
```

### Penjelasan Komponen Arsitektur:
1. **Frontend (React.js via Vite)**:
   - Menangani UI/UX responsif, state management, form rendering, validasi awal (client-side), dan navigasi halaman.
   - Berkomunikasi asynchronously dengan backend menggunakan **Axios** (Header `Authorization: Bearer <Token>` / HTTP Session Cookie).
2. **Backend (Spring Boot)**:
   - Menyediakan REST API endpoint secure & Session/Token handling.
   - Menjalankan logika bisnis (validasi jam presensi Asia/Jakarta, verifikasi izin, pencatatan log audit).
   - **Spring Security** menjaga otorisasi role-based endpoint (`/api/admin/**`, `/api/fasil/**`, `/api/penghuni/**`).
   - **BCrypt** mengamankan seluruh hashing kata sandi.
3. **Database (MySQL)**:
   - Menyimpan seluruh data master, data transaksi presensi, pengajuan izin, dan audit log secara konsisten.
   - Dikonfigurasi dengan Constraint `UNIQUE(user_id, tanggal, jenis_presensi)` untuk mencegah presensi ganda di level basis data.

---

## 4. Rancangan Struktur Folder

```
PRESENSI ASRAMA UNAND/
│
├── docs/                                  # Dokumentasi proyek & perancangan
│   └── TAHAP_1_PERANCANGAN.md
│
├── backend/                               # Project Spring Boot (Java + Maven)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/unand/asrama/
│   │   │   │   ├── config/                # Web Security & CORS Configuration
│   │   │   │   ├── controller/            # REST API Endpoints (@RestController)
│   │   │   │   ├── dto/                   # Request & Response Objects
│   │   │   │   ├── entity/                # JPA Entities (@Entity)
│   │   │   │   ├── exception/             # Custom Global Exception Handler
│   │   │   │   ├── repository/            # JPA Repositories (@Repository)
│   │   │   │   ├── security/              # Security details, UserDetailsService
│   │   │   │   └── service/               # Business Logic Layer (@Service)
│   │   │   └── resources/
│   │   │       ├── application.properties # Database & App Configuration
│   │   │       └── uploads/               # Directory penyimpan bukti izin
│   │   └── test/                          # Unit & Integration Tests
│   └── pom.xml                            # Maven Dependencies
│
└── frontend/                              # Project React.js (Vite + JavaScript)
    ├── src/
    │   ├── assets/                        # Logo UNAND, Gambar, Ikon Static
    │   ├── components/                    # Reusable UI (Button, Modal, Card, Table, Badge, Alert)
    │   ├── contexts/                      # AuthContext, ToastContext
    │   ├── hooks/                         # Custom Hooks (useAuth, useFetch)
    │   ├── layouts/                       # MainLayout, AuthLayout, DashboardLayout
    │   ├── pages/                         # Page Components (Login, Dashboard, Presensi, Izin, Users, dll)
    │   ├── routes/                        # AppRoutes & ProtectedRoute wrapper
    │   ├── services/                      # Axios Instance & API Calls (authService, presensiService, dll)
    │   ├── styles/                        # Modular CSS / Global CSS Tokens
    │   └── utils/                         # Date formatter, Time check utilities
    ├── index.html
    ├── package.json
    └── vite.config.js
```

---

## 5. Perancangan Database (ERD & Schema Sesuai Gambar)

### Entity-Relationship Diagram (ERD)

```
 [ gedung ] 1 ------- N [ kamar ] 1 ------- N [ user ] 1 ------- N [ presensi ]
                                                |
                                                +------- N [ izin ]
```

### Detail Struktur Tabel MySQL (Sesuai Diagram ERD Resmi):

#### 1. Tabel `gedung`
- `id` (INT, Primary Key, Auto Increment)
- `nama` (VARCHAR(100), NOT NULL)

#### 2. Tabel `kamar`
- `id` (INT, Primary Key, Auto Increment)
- `no_kamar` (VARCHAR(20), NOT NULL)
- `lantai` (INT, NOT NULL)
- `id_gedung` (INT, FK -> gedung.id, NOT NULL)

#### 3. Tabel `user`
- `id` (INT, Primary Key, Auto Increment)
- `nim` (VARCHAR(20), NULLABLE / UNIQUE for Penghuni)
- `nama` (VARCHAR(100), NOT NULL)
- `asal` (VARCHAR(100), NULLABLE)
- `jenis_kelamin` (ENUM('L', 'P'), NULLABLE)
- `email` (VARCHAR(100), NOT NULL, UNIQUE)
- `password` (VARCHAR(255), NOT NULL) -- Hashed BCrypt
- `role` (ENUM('Penghuni', 'Fasil', 'Admin'), NOT NULL)
- `id_kamar` (INT, FK -> kamar.id, NULLABLE)

#### 4. Tabel `presensi`
- `id` (INT, Primary Key, Auto Increment)
- `user_id` (INT, FK -> user.id, NOT NULL)
- `tanggal` (DATE, NOT NULL)
- `waktu_absen` (DATETIME, NOT NULL)
- `latitude` (DECIMAL(10,8), NULLABLE)
- `longitude` (DECIMAL(11,8), NULLABLE)
- `status` (ENUM('Hadir', 'Izin', 'Sakit', 'Alpha'), NOT NULL)
- `foto_wajah` (VARCHAR(255), NULLABLE)
- `keterangan` (TEXT, NULLABLE) -- Dapat mencatat jenis presensi ("Subuh" / "Malam") & catatan koreksi

#### 5. Tabel `izin`
- `id` (INT, Primary Key, Auto Increment)
- `user_id` (INT, FK -> user.id, NOT NULL)
- `tgl_mulai` (DATE, NOT NULL)
- `tgl_selesai` (DATE, NOT NULL)
- `alasan` (TEXT, NOT NULL)
- `file_bukti` (VARCHAR(255), NULLABLE) -- Path file upload (ex: uploads/izin_123.jpg)
- `status` (ENUM('Menunggu', 'Disetujui', 'Ditolak'), NOT NULL, DEFAULT 'Menunggu')
- `created_at` (DATETIME, NOT NULL, DEFAULT CURRENT_TIMESTAMP)

---

## 6. Rancangan REST API Backend

Format standar JSON response backend:
```json
{
  "success": true,
  "message": "Pesan deskriptif",
  "data": { ... }
}
```

### A. Endpoint Autentikasi (`/api/auth`)
| Method | URL Path | Role Access | Deskripsi & Validation | Status Code |
|---|---|---|---|---|
| `POST` | `/api/auth/login` | Public | Login pengguna (`email`/`identitas_no`, `password`) | 200 OK / 401 Unauthorized |
| `POST` | `/api/auth/logout` | Authenticated | Invalidate Session / Token | 200 OK |
| `GET` | `/api/auth/me` | Authenticated | Ambil profil user yang sedang login | 200 OK |

### B. Endpoint Presensi (`/api/presensi`)
| Method | URL Path | Role Access | Deskripsi & Validation | Status Code |
|---|---|---|---|---|
| `POST` | `/api/presensi` | Penghuni | Absen Subuh/Malam. Backend cek server time (Asia/Jakarta) & duplicate check | 201 Created / 400 Bad Request |
| `GET` | `/api/presensi/me` | Penghuni | Riwayat presensi user login (+ Query Params: `tanggal_mulai`, `tanggal_selesai`, `jenis`, `page`, `size`) | 200 OK |
| `GET` | `/api/presensi/me/status-hari-ini` | Penghuni | Cek status Subuh & Malam hari ini | 200 OK |
| `GET` | `/api/presensi/fasil/binaan` | Fasil | Riwayat presensi penghuni binaan Fasil | 200 OK |
| `GET` | `/api/presensi/admin/semua` | Admin | Seluruh data presensi asrama (+ Filter & Pagination) | 200 OK |
| `PATCH`| `/api/presensi/{id}/koreksi` | Fasil, Admin | Koreksi status presensi. Body: `{ status_baru, alasan_koreksi }` -> Triggers Audit Log | 200 OK / 404 Not Found |

### C. Endpoint Pengajuan Izin (`/api/izin`)
| Method | URL Path | Role Access | Deskripsi & Validation | Status Code |
|---|---|---|---|---|
| `POST` | `/api/izin` | Penghuni | Ajukan izin + multipart/form-data `file_bukti` (validasi JPG/PNG, max 2MB) | 201 Created / 400 Bad Request |
| `GET` | `/api/izin/me` | Penghuni | Daftar pengajuan izin personal | 200 OK |
| `GET` | `/api/izin/fasil/pending` | Fasil | Daftar izin penghuni binaan berstatus `MENUNGGU` | 200 OK |
| `PATCH`| `/api/izin/{id}/keputusan` | Fasil, Admin | Setujui / Tolak izin. Body: `{ status: "DISETUJUI"/"DITOLAK", catatan }` | 200 OK / 400 Bad Request |

### D. Endpoint User Management & Master Data (`/api/users`, `/api/gedung`, `/api/kamar`)
| Method | URL Path | Role Access | Deskripsi | Status Code |
|---|---|---|---|---|
| `GET` | `/api/users` | Admin | Mengambil daftar pengguna (+ filter role/kamar) | 200 OK |
| `POST` | `/api/users` | Admin | Tambah akun baru (Penghuni / Fasil / Admin) | 201 Created |
| `PUT` | `/api/users/{id}` | Admin | Edit data pengguna | 200 OK |
| `PATCH`| `/api/users/{id}/status` | Admin | Ubah status akun (AKTIF / NONAKTIF) | 200 OK |
| `GET/POST`| `/api/gedung` | Admin | Management Master Data Gedung | 200/201 |
| `GET/POST`| `/api/kamar` | Admin | Management Master Data Kamar | 200/201 |

---

## 7. Rancangan Tampilan Frontend (UI/UX & Responsivitas)

### Palette Warna & Tema Visual (Campus Admin Modern):
- **Primary Color**: Deep Slate Blue (`#1E3A8A`) & Royal Blue (`#2563EB`)
- **Background**: Soft Gray (`#F8FAFC`) & Pure White (`#FFFFFF`)
- **Accent & Status**:
  - Success/Hadir: Soft Emerald (`#10B981`)
  - Warning/Pending: Warm Amber (`#F59E0B`)
  - Danger/Alpha/Ditolak: Crimson Red (`#EF4444`)
  - Info/Izin: Indigo Slate (`#6366F1`)
- **Typography**: Inter / Outfit (Modern Sans-Serif via Google Fonts)

### Layout Responsif:
- **Desktop (Layar > 1024px)**: Sidebar Kiri Persistent, Top Header (Profil & Notifikasi), Card Grid 3-4 kolom.
- **Tablet (768px - 1023px)**: Compact Collapsible Sidebar, Card Grid 2 kolom.
- **Smartphone (Layar < 768px)**: Off-canvas Mobile Menu / Bottom Navigation Bar. Tabel otomatis dikonversi menjadi **Data Card View** agar tidak terpotong (Scroll-free horizontal experience).

---

## 8. Urutan Roadmap Implementasi Proyek (Tahap 2 - 7)

```
[ TAHAP 1 ] ✅ Perancangan Arsitektur, Database, API, & Wireframe UI (SELESAI)
     ↓
[ TAHAP 2 ] ⏳ Persiapan Lingkungan & Pengecekan Software (Java, Node, MySQL, VS Code Ext)
     ↓
[ TAHAP 3 ] ⚙️ Development Backend Spring Boot Core (Entities, Spring Security, Services, REST Controllers)
     ↓
[ TAHAP 4 ] 🎨 Development Frontend React.js (Vite, Layouts, CSS Modules, Components, Dashboard Pages)
     ↓
[ TAHAP 5 ] 🔗 Integrasi REST API Frontend <-> Backend (Axios, Session Handling, Error Handlers)
     ↓
[ TAHAP 6 ] 🧪 Pengujian End-to-End & Boundary Rules (Jadwal Presensi, File Upload, Duplicate Checks)
     ↓
[ TAHAP 7 ] 📚 Dokumentasi Akhir, Database Dump, & Panduan Penyerahan Proyek
```
