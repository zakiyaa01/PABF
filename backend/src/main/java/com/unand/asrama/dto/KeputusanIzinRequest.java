package com.unand.asrama.dto;

import com.unand.asrama.entity.Izin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class KeputusanIzinRequest {

    @NotNull(message = "Status keputusan wajib diisi")
    private Izin.StatusIzin status;

    @NotBlank(message = "Catatan Fasil wajib diisi")
    private String catatanFasil;

    public KeputusanIzinRequest() {
    }

    public KeputusanIzinRequest(Izin.StatusIzin status, String catatanFasil) {
        this.status = status;
        this.catatanFasil = catatanFasil;
    }

    public Izin.StatusIzin getStatus() {
        return status;
    }

    public void setStatus(Izin.StatusIzin status) {
        this.status = status;
    }

    public String getCatatanFasil() {
        return catatanFasil;
    }

    public void setCatatanFasil(String catatanFasil) {
        this.catatanFasil = catatanFasil;
    }
}
