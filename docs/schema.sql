-- ========================================================
-- Database Schema Script: Sistem Informasi Absensi Asrama UNAND (SIA-Asrama)
-- Enterprise Anti-Cheat, Geofencing, Biometric Device Binding & Audit Log
-- ========================================================

CREATE DATABASE IF NOT EXISTS `asrama_unand_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `asrama_unand_db`;

-- 1. Table: gedung
CREATE TABLE IF NOT EXISTS `gedung` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(100) NOT NULL,
  `latitude` DECIMAL(10,8) NOT NULL DEFAULT -0.91423100,
  `longitude` DECIMAL(11,8) NOT NULL DEFAULT 100.46601200,
  `radius_meter` DOUBLE NOT NULL DEFAULT 50.0,
  `wifi_bssid` VARCHAR(50) DEFAULT '70:3a:0e:aa:bb:cc'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table: kamar
CREATE TABLE IF NOT EXISTS `kamar` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `no_kamar` VARCHAR(20) NOT NULL,
  `lantai` INT NOT NULL,
  `lorong` VARCHAR(50) DEFAULT 'Lorong 1',
  `id_gedung` INT NOT NULL,
  CONSTRAINT `fk_kamar_gedung` FOREIGN KEY (`id_gedung`) REFERENCES `gedung` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table: user
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nim` VARCHAR(30) UNIQUE NULL,
  `nama` VARCHAR(100) NOT NULL,
  `asal` VARCHAR(100) NULL,
  `jenis_kelamin` ENUM('L', 'P') NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` ENUM('Penghuni', 'Fasil', 'Admin') NOT NULL,
  `id_kamar` INT NULL,
  `fasil_id` INT NULL,
  `lorong_binaan` VARCHAR(100) NULL,
  `device_id` VARCHAR(100) NULL,
  `biometric_enabled` BOOLEAN DEFAULT FALSE,
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT `fk_user_kamar` FOREIGN KEY (`id_kamar`) REFERENCES `kamar` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_user_fasil` FOREIGN KEY (`fasil_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Table: presensi
CREATE TABLE IF NOT EXISTS `presensi` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `tanggal` DATE NOT NULL,
  `waktu_absen` DATETIME NOT NULL,
  `jenis_presensi` ENUM('SUBUH', 'MALAM') NOT NULL DEFAULT 'SUBUH',
  `latitude` DECIMAL(10,8) NULL,
  `longitude` DECIMAL(11,8) NULL,
  `accuracy_meters` DOUBLE NULL,
  `distance_meters` DOUBLE NULL,
  `is_mock_location` BOOLEAN DEFAULT FALSE,
  `is_emulator` BOOLEAN DEFAULT FALSE,
  `wifi_bssid` VARCHAR(50) NULL,
  `device_id` VARCHAR(100) NULL,
  `device_timestamp` VARCHAR(50) NULL,
  `status` ENUM('Hadir', 'Terlambat', 'Izin', 'Sakit', 'Alpha') NOT NULL,
  `foto_wajah` VARCHAR(500) NULL,
  `keterangan` TEXT NULL,
  CONSTRAINT `fk_presensi_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `uq_presensi_user_tanggal_jenis` UNIQUE (`user_id`, `tanggal`, `jenis_presensi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Table: audit_log (Immutable Audit Trail Log)
CREATE TABLE IF NOT EXISTS `audit_log` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `presensi_id` INT NOT NULL,
  `fasil_id` INT NOT NULL,
  `fasil_nama` VARCHAR(100) NOT NULL,
  `timestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status_lama` VARCHAR(20) NOT NULL,
  `status_baru` VARCHAR(20) NOT NULL,
  `ip_address` VARCHAR(50) NULL,
  `alasan` TEXT NOT NULL,
  CONSTRAINT `fk_audit_presensi` FOREIGN KEY (`presensi_id`) REFERENCES `presensi` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Table: izin
CREATE TABLE IF NOT EXISTS `izin` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `tgl_mulai` DATE NOT NULL,
  `tgl_selesai` DATE NOT NULL,
  `kategori` ENUM('Sakit', 'PulangKampung', 'Dispensasi', 'DaruratKeluarga', 'Lainnya') NOT NULL DEFAULT 'Lainnya',
  `alasan` TEXT NOT NULL,
  `file_bukti` VARCHAR(500) NULL,
  `status` ENUM('Menunggu', 'Disetujui', 'Ditolak') NOT NULL DEFAULT 'Menunggu',
  `catatan_fasil` TEXT NULL,
  `fasil_id` INT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` DATETIME NULL,
  CONSTRAINT `fk_izin_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_izin_fasil` FOREIGN KEY (`fasil_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
