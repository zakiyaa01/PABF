package com.unand.asrama.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PresensiRequest {

    @NotNull(message = "User ID wajib diisi")
    private Integer userId;

    private String jenisPresensi; // "Subuh" atau "Malam"
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String keterangan;

    public PresensiRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getJenisPresensi() {
        return jenisPresensi;
    }

    public void setJenisPresensi(String jenisPresensi) {
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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
