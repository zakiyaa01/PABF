package com.unand.asrama.dto;

import com.unand.asrama.entity.Presensi;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class KoreksiPresensiRequest {

    @NotNull(message = "Status baru wajib dipilih")
    private Presensi.StatusPresensi statusBaru;

    @NotBlank(message = "Alasan koreksi presensi wajib diisi")
    private String alasan;

    public KoreksiPresensiRequest() {
    }

    public KoreksiPresensiRequest(Presensi.StatusPresensi statusBaru, String alasan) {
        this.statusBaru = statusBaru;
        this.alasan = alasan;
    }

    public Presensi.StatusPresensi getStatusBaru() {
        return statusBaru;
    }

    public void setStatusBaru(Presensi.StatusPresensi statusBaru) {
        this.statusBaru = statusBaru;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }
}
