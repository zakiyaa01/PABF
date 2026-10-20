package com.unand.asrama.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "presensi")
public class Presensi {

    public enum StatusPresensi {
        Hadir, Terlambat, Izin, Sakit, Alpha
    }

    public enum JenisPresensi {
        SUBUH, MALAM
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tanggal", nullable = false)
    private LocalDate tanggal;

    @Column(name = "waktu_absen", nullable = false)
    private LocalDateTime waktuAbsen;

    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_presensi", length = 20)
    private JenisPresensi jenisPresensi = JenisPresensi.SUBUH;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "accuracy_meters")
    private Double accuracyMeters;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "is_mock_location")
    private Boolean isMockLocation = false;

    @Column(name = "is_emulator")
    private Boolean isEmulator = false;

    @Column(name = "wifi_bssid", length = 50)
    private String wifiBssid;

    @Column(name = "device_id", length = 100)
    private String deviceId;

    @Column(name = "device_timestamp", length = 50)
    private String deviceTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPresensi status;

    @Column(name = "foto_wajah", length = 500)
    private String fotoWajah;

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;

    public Presensi() {
    }

    public Presensi(User user, LocalDate tanggal, LocalDateTime waktuAbsen, JenisPresensi jenisPresensi, StatusPresensi status, String keterangan) {
        this.user = user;
        this.tanggal = tanggal;
        this.waktuAbsen = waktuAbsen;
        this.jenisPresensi = jenisPresensi;
        this.status = status;
        this.keterangan = keterangan;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalDateTime getWaktuAbsen() {
        return waktuAbsen;
    }

    public void setWaktuAbsen(LocalDateTime waktuAbsen) {
        this.waktuAbsen = waktuAbsen;
    }

    public JenisPresensi getJenisPresensi() {
        return jenisPresensi;
    }

    public void setJenisPresensi(JenisPresensi jenisPresensi) {
        this.jenisPresensi = jenisPresensi;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Double getAccuracyMeters() {
        return accuracyMeters;
    }

    public void setAccuracyMeters(Double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
    }

    public Double getDistanceMeters() {
        return distanceMeters;
    }

    public void setDistanceMeters(Double distanceMeters) {
        this.distanceMeters = distanceMeters;
    }

    public Boolean getIsMockLocation() {
        return isMockLocation;
    }

    public void setIsMockLocation(Boolean isMockLocation) {
        this.isMockLocation = isMockLocation;
    }

    public Boolean getIsEmulator() {
        return isEmulator;
    }

    public void setIsEmulator(Boolean isEmulator) {
        this.isEmulator = isEmulator;
    }

    public String getWifiBssid() {
        return wifiBssid;
    }

    public void setWifiBssid(String wifiBssid) {
        this.wifiBssid = wifiBssid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceTimestamp() {
        return deviceTimestamp;
    }

    public void setDeviceTimestamp(String deviceTimestamp) {
        this.deviceTimestamp = deviceTimestamp;
    }

    public StatusPresensi getStatus() {
        return status;
    }

    public void setStatus(StatusPresensi status) {
        this.status = status;
    }

    public String getFotoWajah() {
        return fotoWajah;
    }

    public void setFotoWajah(String fotoWajah) {
        this.fotoWajah = fotoWajah;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
