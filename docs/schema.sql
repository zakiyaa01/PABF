-- Database Creation Script for Sistem Informasi Absensi Asrama UNAND
CREATE DATABASE IF NOT EXISTS `asrama_unand_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `asrama_unand_db`;

-- 1. Table: gedung
CREATE TABLE IF NOT EXISTS `gedung` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nama` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Table: kamar
CREATE TABLE IF NOT EXISTS `kamar` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `no_kamar` VARCHAR(20) NOT NULL,
  `lantai` INT NOT NULL,
  `id_gedung` INT NOT NULL,
  CONSTRAINT `fk_kamar_gedung` FOREIGN KEY (`id_gedung`) REFERENCES `gedung` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Table: user
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `nim` VARCHAR(20) UNIQUE NULL,
  `nama` VARCHAR(100) NOT NULL,
  `asal` VARCHAR(100) NULL,
  `jenis_kelamin` ENUM('L', 'P') NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  `role` ENUM('Penghuni', 'Fasil', 'Admin') NOT NULL,
  `id_kamar` INT NULL,
  CONSTRAINT `fk_user_kamar` FOREIGN KEY (`id_kamar`) REFERENCES `kamar` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Table: presensi
CREATE TABLE IF NOT EXISTS `presensi` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `tanggal` DATE NOT NULL,
  `waktu_absen` DATETIME NOT NULL,
  `latitude` DECIMAL(10,8) NULL,
  `longitude` DECIMAL(11,8) NULL,
  `status` ENUM('Hadir', 'Izin', 'Sakit', 'Alpha') NOT NULL,
  `foto_wajah` VARCHAR(255) NULL,
  `keterangan` TEXT NULL,
  CONSTRAINT `fk_presensi_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Table: izin
CREATE TABLE IF NOT EXISTS `izin` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `tgl_mulai` DATE NOT NULL,
  `tgl_selesai` DATE NOT NULL,
  `alasan` TEXT NOT NULL,
  `file_bukti` VARCHAR(255) NULL,
  `status` ENUM('Menunggu', 'Disetujui', 'Ditolak') NOT NULL DEFAULT 'Menunggu',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_izin_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
