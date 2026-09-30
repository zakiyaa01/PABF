package com.unand.asrama.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class IzinRequest {

    @NotNull(message = "User ID wajib diisi")
    private Integer userId;

    @NotNull(message = "Tanggal mulai wajib diisi")
    private LocalDate tglMulai;

    @NotNull(message = "Tanggal selesai wajib diisi")
    private LocalDate tglSelesai;

    @NotBlank(message = "Alasan izin wajib diisi")
    private String alasan;

    public IzinRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
}
