package com.unand.asrama.service;

import com.unand.asrama.dto.UserManageRequest;
import com.unand.asrama.entity.Gedung;
import com.unand.asrama.entity.Kamar;
import com.unand.asrama.entity.Presensi;
import com.unand.asrama.entity.User;
import com.unand.asrama.repository.GedungRepository;
import com.unand.asrama.repository.KamarRepository;
import com.unand.asrama.repository.PresensiRepository;
import com.unand.asrama.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MasterDataService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GedungRepository gedungRepository;

    @Autowired
    private KamarRepository kamarRepository;

    @Autowired
    private PresensiRepository presensiRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers(User.Role role) {
        if (role != null) {
            return userRepository.findByRole(role);
        }
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengguna ID " + id + " tidak ditemukan!"));
    }

    @Transactional
    public User createUser(UserManageRequest request) {
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email '" + request.getEmail() + "' sudah terdaftar!");
        }
        if (request.getNim() != null && !request.getNim().isEmpty() && userRepository.findByNim(request.getNim()).isPresent()) {
            throw new RuntimeException("NIM '" + request.getNim() + "' sudah terdaftar!");
        }

        User user = new User();
        user.setNim(request.getNim());
        user.setNama(request.getNama());
        user.setAsal(request.getAsal());
        user.setJenisKelamin(request.getJenisKelamin());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword() != null && !request.getPassword().isEmpty() ? request.getPassword() : "asrama123"));
        user.setRole(request.getRole() != null ? request.getRole() : User.Role.Penghuni);
        user.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        user.setDeviceId(request.getDeviceId());
        user.setLorongBinaan(request.getLorongBinaan());

        if (request.getKamarId() != null) {
            kamarRepository.findById(request.getKamarId()).ifPresent(user::setKamar);
        }
        if (request.getFasilId() != null) {
            userRepository.findById(request.getFasilId()).ifPresent(user::setFasil);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Integer id, UserManageRequest request) {
        User user = getUserById(id);

        if (request.getNama() != null) user.setNama(request.getNama());
        if (request.getNim() != null) user.setNim(request.getNim());
        if (request.getAsal() != null) user.setAsal(request.getAsal());
        if (request.getJenisKelamin() != null) user.setJenisKelamin(request.getJenisKelamin());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getIsActive() != null) user.setIsActive(request.getIsActive());
        if (request.getDeviceId() != null) user.setDeviceId(request.getDeviceId().isEmpty() ? null : request.getDeviceId());
        if (request.getLorongBinaan() != null) user.setLorongBinaan(request.getLorongBinaan());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        if (request.getKamarId() != null) {
            kamarRepository.findById(request.getKamarId()).ifPresent(user::setKamar);
        }

        if (request.getFasilId() != null) {
            userRepository.findById(request.getFasilId()).ifPresent(user::setFasil);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void resetPassword(Integer id, String newPassword) {
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword != null ? newPassword : "password123"));
        userRepository.save(user);
    }

    @Transactional
    public User toggleActive(Integer id) {
        User user = getUserById(id);
        user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
        return userRepository.save(user);
    }

    // Dynamic Mapping: Assign Fasil to Lorong
    @Transactional
    public void assignFasilToLorong(Integer fasilId, String lorong) {
        User fasil = getUserById(fasilId);
        fasil.setLorongBinaan(lorong);
        userRepository.save(fasil);

        // Optionally associate all residents in that lorong to this fasil
        List<User> penghuniList = userRepository.findByRole(User.Role.Penghuni);
        for (User p : penghuniList) {
            if (p.getKamar() != null && lorong.equalsIgnoreCase(p.getKamar().getLorong())) {
                p.setFasil(fasil);
                userRepository.save(p);
            }
        }
    }

    // System-Wide Analytics
    public Map<String, Object> getSystemAnalytics() {
        Map<String, Object> stats = new HashMap<>();

        long totalPenghuni = userRepository.findByRole(User.Role.Penghuni).size();
        long totalFasil = userRepository.findByRole(User.Role.Fasil).size();
        long totalGedung = gedungRepository.count();
        long totalKamar = kamarRepository.count();

        LocalDate today = LocalDate.now();
        long hadirToday = presensiRepository.countByTanggalAndStatus(today, Presensi.StatusPresensi.Hadir);
        long terlambatToday = presensiRepository.countByTanggalAndStatus(today, Presensi.StatusPresensi.Terlambat);
        long izinToday = presensiRepository.countByTanggalAndStatus(today, Presensi.StatusPresensi.Izin);
        long alphaToday = presensiRepository.countByTanggalAndStatus(today, Presensi.StatusPresensi.Alpha);

        stats.put("totalPenghuni", totalPenghuni);
        stats.put("totalFasil", totalFasil);
        stats.put("totalGedung", totalGedung);
        stats.put("totalKamar", totalKamar);
        stats.put("hadirToday", hadirToday);
        stats.put("terlambatToday", terlambatToday);
        stats.put("izinToday", izinToday);
        stats.put("alphaToday", alphaToday);

        double rate = totalPenghuni > 0 ? ((double) (hadirToday + terlambatToday) / (totalPenghuni * 2)) * 100 : 0.0;
        stats.put("attendancePercentageToday", Math.round(rate * 10.0) / 10.0);

        // Attendance by Gedung
        List<Gedung> gedungs = gedungRepository.findAll();
        List<Map<String, Object>> gedungStats = new ArrayList<>();
        for (Gedung g : gedungs) {
            Map<String, Object> gMap = new HashMap<>();
            gMap.put("id", g.getId());
            gMap.put("nama", g.getNama());
            long countPenghuniInGedung = userRepository.findByRole(User.Role.Penghuni).stream()
                    .filter(u -> u.getKamar() != null && u.getKamar().getGedung() != null && u.getKamar().getGedung().getId().equals(g.getId()))
                    .count();
            gMap.put("totalPenghuni", countPenghuniInGedung);
            gedungStats.add(gMap);
        }
        stats.put("gedungStats", gedungStats);

        return stats;
    }

    // Export Penghuni to Excel (.xlsx)
    public byte[] exportPenghuniToExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Data Penghuni Asrama");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"No", "NIM", "Nama Lengkap", "Asal Daerah", "L/P", "Email", "Gedung", "Lantai", "Lorong", "No. Kamar", "Fasil Pembina", "Device ID", "Status Akun"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<User> penghuniList = userRepository.findByRole(User.Role.Penghuni);
            int rowIdx = 1;
            for (User u : penghuniList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(u.getNim() != null ? u.getNim() : "-");
                row.createCell(2).setCellValue(u.getNama());
                row.createCell(3).setCellValue(u.getAsal() != null ? u.getAsal() : "-");
                row.createCell(4).setCellValue(u.getJenisKelamin() != null ? u.getJenisKelamin().name() : "-");
                row.createCell(5).setCellValue(u.getEmail());

                if (u.getKamar() != null) {
                    row.createCell(6).setCellValue(u.getKamar().getGedung() != null ? u.getKamar().getGedung().getNama() : "-");
                    row.createCell(7).setCellValue(u.getKamar().getLantai());
                    row.createCell(8).setCellValue(u.getKamar().getLorong() != null ? u.getKamar().getLorong() : "-");
                    row.createCell(9).setCellValue(u.getKamar().getNoKamar());
                } else {
                    row.createCell(6).setCellValue("-");
                    row.createCell(7).setCellValue("-");
                    row.createCell(8).setCellValue("-");
                    row.createCell(9).setCellValue("-");
                }

                row.createCell(10).setCellValue(u.getFasil() != null ? u.getFasil().getNama() : "Belum Dialokasikan");
                row.createCell(11).setCellValue(u.getDeviceId() != null ? u.getDeviceId() : "Belum Terikat");
                row.createCell(12).setCellValue(Boolean.TRUE.equals(u.getIsActive()) ? "AKTIF" : "NONAKTIF");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengekspor data Excel: " + e.getMessage());
        }
    }

    // Export Presensi Rekapitulasi Global to Excel (.xlsx)
    public byte[] exportPresensiToExcel(LocalDate startDate, LocalDate endDate) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Rekapitulasi Presensi");

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"No", "Tanggal", "Waktu Absen", "NIM", "Nama Mahasiswa", "Gedung", "Kamar", "Sesi", "Status", "Jarak (m)", "Mock GPS", "Emulator", "Keterangan"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            LocalDate start = startDate != null ? startDate : LocalDate.now().minusDays(30);
            LocalDate end = endDate != null ? endDate : LocalDate.now();

            List<Presensi> presensiList = presensiRepository.findByTanggalBetweenOrderByWaktuAbsenDesc(start, end);
            int rowIdx = 1;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (Presensi p : presensiList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowIdx - 1);
                row.createCell(1).setCellValue(p.getTanggal().toString());
                row.createCell(2).setCellValue(p.getWaktuAbsen().format(dtf));
                row.createCell(3).setCellValue(p.getUser().getNim() != null ? p.getUser().getNim() : "-");
                row.createCell(4).setCellValue(p.getUser().getNama());

                String gedungName = "-";
                String kamarNo = "-";
                if (p.getUser().getKamar() != null) {
                    kamarNo = p.getUser().getKamar().getNoKamar();
                    if (p.getUser().getKamar().getGedung() != null) {
                        gedungName = p.getUser().getKamar().getGedung().getNama();
                    }
                }
                row.createCell(5).setCellValue(gedungName);
                row.createCell(6).setCellValue(kamarNo);
                row.createCell(7).setCellValue(p.getJenisPresensi().name());
                row.createCell(8).setCellValue(p.getStatus().name());
                row.createCell(9).setCellValue(p.getDistanceMeters() != null ? Math.round(p.getDistanceMeters()) : 0);
                row.createCell(10).setCellValue(Boolean.TRUE.equals(p.getIsMockLocation()) ? "YA (TERDETEKSI)" : "TIDAK");
                row.createCell(11).setCellValue(Boolean.TRUE.equals(p.getIsEmulator()) ? "YA (EMULATOR)" : "TIDAK");
                row.createCell(12).setCellValue(p.getKeterangan() != null ? p.getKeterangan() : "-");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengekspor rekapitulasi presensi: " + e.getMessage());
        }
    }

    // Mass Import Penghuni from Excel (.xlsx)
    @Transactional
    public int importPenghuniFromExcel(InputStream inputStream) {
        int importedCount = 0;
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            if (rows.hasNext()) {
                rows.next(); // Skip header row
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                if (row.getCell(1) == null) continue;

                String nim = getCellValueAsString(row.getCell(1));
                String nama = getCellValueAsString(row.getCell(2));
                String asal = getCellValueAsString(row.getCell(3));
                String jkStr = getCellValueAsString(row.getCell(4));
                String email = getCellValueAsString(row.getCell(5));

                if (nim.isEmpty() || email.isEmpty()) continue;

                Optional<User> existing = userRepository.findByEmailOrNim(email, nim);
                User user = existing.orElse(new User());

                user.setNim(nim);
                user.setNama(nama);
                user.setAsal(asal);
                user.setEmail(email);
                user.setRole(User.Role.Penghuni);
                user.setIsActive(true);

                if (user.getPassword() == null) {
                    user.setPassword(passwordEncoder.encode("penghuni123"));
                }

                if ("L".equalsIgnoreCase(jkStr)) {
                    user.setJenisKelamin(User.JenisKelamin.L);
                } else if ("P".equalsIgnoreCase(jkStr)) {
                    user.setJenisKelamin(User.JenisKelamin.P);
                }

                userRepository.save(user);
                importedCount++;
            }
        } catch (Exception e) {
            throw new RuntimeException("Gagal membaca file Excel: " + e.getMessage());
        }
        return importedCount;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return "";
        }
    }
}
