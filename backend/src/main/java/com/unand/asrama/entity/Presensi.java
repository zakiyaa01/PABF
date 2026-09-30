package com.unand.asrama.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "presensi")
public class Presensi {

    public enum StatusPresensi {
        Hadir, Izin, Sakit, Alpha
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

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusPresensi status;

    @Column(name = "foto_wajah", length = 255)
    private String fotoWajah;

    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;

    public Presensi() {
    }

    public Presensi(User user, LocalDate tanggal, LocalDateTime waktuAbsen, StatusPresensi status, String keterangan) {
        this.user = user;
        this.tanggal = tanggal;
        this.waktuAbsen = waktuAbsen;
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
