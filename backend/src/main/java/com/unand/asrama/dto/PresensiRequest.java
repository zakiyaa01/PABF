package com.unand.asrama.dto;

import java.math.BigDecimal;

public class PresensiRequest {

    private String nim;
    private Integer userId; // Optional fallback
    private String jenisPresensi; // "SUBUH" or "MALAM"
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double accuracyMeters;
    private Boolean isMockLocation = false;
    private Boolean isEmulator = false;
    private String wifiBssid;
    private String deviceDeviceId; // Matches payload key deviceDeviceId / deviceId
    private String deviceTimestamp;
    private String photoBase64;
    private String keterangan;

    public PresensiRequest() {
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
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

    public Double getAccuracyMeters() {
        return accuracyMeters;
    }

    public void setAccuracyMeters(Double accuracyMeters) {
        this.accuracyMeters = accuracyMeters;
    }

    public Boolean getIsMockLocation() {
        return isMockLocation;
    }

    public void setIsMockLocation(Boolean isMockLocation) {
        this.isMockLocation = isMockLocation;
    }

    public Boolean getIsEmulator() {
        return isEmulator;
    }

    public void setIsEmulator(Boolean isEmulator) {
        this.isEmulator = isEmulator;
    }

    public String getWifiBssid() {
        return wifiBssid;
    }

    public void setWifiBssid(String wifiBssid) {
        this.wifiBssid = wifiBssid;
    }

    public String getDeviceDeviceId() {
        return deviceDeviceId;
    }

    public void setDeviceDeviceId(String deviceDeviceId) {
        this.deviceDeviceId = deviceDeviceId;
    }

    public String getDeviceTimestamp() {
        return deviceTimestamp;
    }

    public void setDeviceTimestamp(String deviceTimestamp) {
        this.deviceTimestamp = deviceTimestamp;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
