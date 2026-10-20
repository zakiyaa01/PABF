package com.unand.asrama.dto;

import com.unand.asrama.entity.User;

public class UserManageRequest {
    private String nim;
    private String nama;
    private String asal;
    private User.JenisKelamin jenisKelamin;
    private String email;
    private String password;
    private User.Role role;
    private Integer kamarId;
    private Integer fasilId;
    private String lorongBinaan;
    private String deviceId;
    private Boolean isActive = true;

    public UserManageRequest() {
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

    public String getAsal() {
        return asal;
    }

    public void setAsal(String asal) {
        this.asal = asal;
    }

    public User.JenisKelamin getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(User.JenisKelamin jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public Integer getKamarId() {
        return kamarId;
    }

    public void setKamarId(Integer kamarId) {
        this.kamarId = kamarId;
    }

    public Integer getFasilId() {
        return fasilId;
    }

    public void setFasilId(Integer fasilId) {
        this.fasilId = fasilId;
    }

    public String getLorongBinaan() {
        return lorongBinaan;
    }

    public void setLorongBinaan(String lorongBinaan) {
        this.lorongBinaan = lorongBinaan;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
