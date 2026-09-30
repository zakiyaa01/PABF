package com.unand.asrama.dto;

import com.unand.asrama.entity.User;

public class LoginResponse {
    private Integer id;
    private String nim;
    private String nama;
    private String email;
    private User.Role role;
    private User.JenisKelamin jenisKelamin;
    private String asal;
    private String namaGedung;
    private String noKamar;

    public LoginResponse() {
    }

    public LoginResponse(User user) {
        this.id = user.getId();
        this.nim = user.getNim();
        this.nama = user.getNama();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.jenisKelamin = user.getJenisKelamin();
        this.asal = user.getAsal();
        if (user.getKamar() != null) {
            this.noKamar = user.getKamar().getNoKamar();
            if (user.getKamar().getGedung() != null) {
                this.namaGedung = user.getKamar().getGedung().getNama();
            }
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public User.JenisKelamin getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(User.JenisKelamin jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getAsal() {
        return asal;
    }

    public void setAsal(String asal) {
        this.asal = asal;
    }

    public String getNamaGedung() {
        return namaGedung;
    }

    public void setNamaGedung(String namaGedung) {
        this.namaGedung = namaGedung;
    }

    public String getNoKamar() {
        return noKamar;
    }

    public void setNoKamar(String noKamar) {
        this.noKamar = noKamar;
    }
}
