package com.unand.asrama.dto;

import com.unand.asrama.entity.User;

public class LoginResponse {
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    private Integer id;
    private String nim;
    private String nama;
    private String email;
    private User.Role role;
    private User.JenisKelamin jenisKelamin;
    private String asal;
    private String namaGedung;
    private Integer idGedung;
    private String noKamar;
    private String lorong;
    private Integer idKamar;
    private String fasilNama;
    private Integer fasilId;
    private String lorongBinaan;
    private String deviceId;
    private Boolean biometricEnabled;
    private Boolean isActive;

    public LoginResponse() {
    }

    public LoginResponse(User user) {
        populateUser(user);
    }

    public LoginResponse(User user, String token, String refreshToken, Long expiresIn) {
        populateUser(user);
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    private void populateUser(User user) {
        this.id = user.getId();
        this.nim = user.getNim();
        this.nama = user.getNama();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.jenisKelamin = user.getJenisKelamin();
        this.asal = user.getAsal();
        this.deviceId = user.getDeviceId();
        this.biometricEnabled = user.getBiometricEnabled();
        this.isActive = user.getIsActive();
        this.lorongBinaan = user.getLorongBinaan();

        if (user.getFasil() != null) {
            this.fasilId = user.getFasil().getId();
            this.fasilNama = user.getFasil().getNama();
        }

        if (user.getKamar() != null) {
            this.idKamar = user.getKamar().getId();
            this.noKamar = user.getKamar().getNoKamar();
            this.lorong = user.getKamar().getLorong();
            if (user.getKamar().getGedung() != null) {
                this.idGedung = user.getKamar().getGedung().getId();
                this.namaGedung = user.getKamar().getGedung().getNama();
            }
        }
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
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

    public Integer getIdGedung() {
        return idGedung;
    }

    public void setIdGedung(Integer idGedung) {
        this.idGedung = idGedung;
    }

    public String getNoKamar() {
        return noKamar;
    }

    public void setNoKamar(String noKamar) {
        this.noKamar = noKamar;
    }

    public String getLorong() {
        return lorong;
    }

    public void setLorong(String lorong) {
        this.lorong = lorong;
    }

    public Integer getIdKamar() {
        return idKamar;
    }

    public void setIdKamar(Integer idKamar) {
        this.idKamar = idKamar;
    }

    public String getFasilNama() {
        return fasilNama;
    }

    public void setFasilNama(String fasilNama) {
        this.fasilNama = fasilNama;
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

    public Boolean getBiometricEnabled() {
        return biometricEnabled;
    }

    public void setBiometricEnabled(Boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
