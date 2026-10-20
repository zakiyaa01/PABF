package com.unand.asrama.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gedung")
public class Gedung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude = new BigDecimal("-0.914231");

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude = new BigDecimal("100.466012");

    @Column(name = "radius_meter")
    private Double radiusMeter = 50.0;

    @Column(name = "wifi_bssid", length = 50)
    private String wifiBssid = "70:3a:0e:aa:bb:cc";

    public Gedung() {
    }

    public Gedung(String nama) {
        this.nama = nama;
    }

    public Gedung(String nama, BigDecimal latitude, BigDecimal longitude, Double radiusMeter, String wifiBssid) {
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeter = radiusMeter != null ? radiusMeter : 50.0;
        this.wifiBssid = wifiBssid;
    }

    public Gedung(Integer id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
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

    public Double getRadiusMeter() {
        return radiusMeter;
    }

    public void setRadiusMeter(Double radiusMeter) {
        this.radiusMeter = radiusMeter;
    }

    public String getWifiBssid() {
        return wifiBssid;
    }

    public void setWifiBssid(String wifiBssid) {
        this.wifiBssid = wifiBssid;
    }
}
