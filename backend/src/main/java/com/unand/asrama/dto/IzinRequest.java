package com.unand.asrama.dto;

import com.unand.asrama.entity.Izin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class IzinRequest {

    private Integer userId;
    private String nim;

    @NotNull(message = "Tanggal mulai wajib diisi")
    private LocalDate tglMulai;

    @NotNull(message = "Tanggal selesai wajib diisi")
    private LocalDate tglSelesai;

    private Izin.KategoriIzin kategori = Izin.KategoriIzin.Lainnya;

    @NotBlank(message = "Alasan izin wajib diisi")
    private String alasan;

    private String fileBuktiBase64;

    public IzinRequest() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
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

    public Izin.KategoriIzin getKategori() {
        return kategori;
    }

    public void setKategori(Izin.KategoriIzin kategori) {
        this.kategori = kategori;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public String getFileBuktiBase64() {
        return fileBuktiBase64;
    }

    public void setFileBuktiBase64(String fileBuktiBase64) {
        this.fileBuktiBase64 = fileBuktiBase64;
    }
}
