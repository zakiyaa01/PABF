package com.unand.asrama.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gedung")
public class Gedung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    public Gedung() {
    }

    public Gedung(String nama) {
        this.nama = nama;
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
}
