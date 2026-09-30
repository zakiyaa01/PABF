package com.unand.asrama.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "izin")
public class Izin {

    public enum StatusIzin {
        Menunggu, Disetujui, Ditolak
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tgl_mulai", nullable = false)
    private LocalDate tglMulai;

    @Column(name = "tgl_selesai", nullable = false)
    private LocalDate tglSelesai;

    @Column(name = "alasan", nullable = false, columnDefinition = "TEXT")
    private String alasan;

    @Column(name = "file_bukti", length = 255)
    private String fileBukti;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusIzin status = StatusIzin.Menunggu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Izin() {
    }

    public Izin(User user, LocalDate tglMulai, LocalDate tglSelesai, String alasan, String fileBukti) {
        this.user = user;
        this.tglMulai = tglMulai;
        this.tglSelesai = tglSelesai;
        this.alasan = alasan;
        this.fileBukti = fileBukti;
        this.status = StatusIzin.Menunggu;
        this.createdAt = LocalDateTime.now();
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

    public LocalDate getTglMulai() {
        return tglMulai;
    }

    public void setTglMulai(LocalDate tglMulai) {
        this.tglMulai = tglMulai;
    }

    public LocalDate getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(LocalDate tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getFileBukti() {
        return fileBukti;
    }

    public void setFileBukti(String fileBukti) {
        this.fileBukti = fileBukti;
    }

    public StatusIzin getStatus() {
        return status;
    }

    public void setStatus(StatusIzin status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
