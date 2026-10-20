package com.unand.asrama.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "presensi_id", nullable = false)
    private Presensi presensi;

    @Column(name = "fasil_id", nullable = false)
    private Integer fasilId;

    @Column(name = "fasil_nama", nullable = false, length = 100)
    private String fasilNama;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "status_lama", nullable = false, length = 20)
    private String statusLama;

    @Column(name = "status_baru", nullable = false, length = 20)
    private String statusBaru;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "alasan", nullable = false, columnDefinition = "TEXT")
    private String alasan;

    public AuditLog() {
    }

    public AuditLog(Presensi presensi, Integer fasilId, String fasilNama, String statusLama, String statusBaru, String ipAddress, String alasan) {
        this.presensi = presensi;
        this.fasilId = fasilId;
        this.fasilNama = fasilNama;
        this.statusLama = statusLama;
        this.statusBaru = statusBaru;
        this.ipAddress = ipAddress;
        this.alasan = alasan;
        this.timestamp = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Presensi getPresensi() {
        return presensi;
    }

    public void setPresensi(Presensi presensi) {
        this.presensi = presensi;
    }

    public Integer getFasilId() {
        return fasilId;
    }

    public void setFasilId(Integer fasilId) {
        this.fasilId = fasilId;
    }

    public String getFasilNama() {
        return fasilNama;
    }

    public void setFasilNama(String fasilNama) {
        this.fasilNama = fasilNama;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusLama() {
        return statusLama;
    }

    public void setStatusLama(String statusLama) {
        this.statusLama = statusLama;
    }

    public String getStatusBaru() {
        return statusBaru;
    }

    public void setStatusBaru(String statusBaru) {
        this.statusBaru = statusBaru;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
