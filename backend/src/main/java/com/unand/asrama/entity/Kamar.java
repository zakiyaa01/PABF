package com.unand.asrama.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kamar")
public class Kamar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "no_kamar", nullable = false, length = 20)
    private String noKamar;

    @Column(name = "lantai", nullable = false)
    private Integer lantai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_gedung", nullable = false)
    private Gedung gedung;

    public Kamar() {
    }

    public Kamar(String noKamar, Integer lantai, Gedung gedung) {
        this.noKamar = noKamar;
        this.lantai = lantai;
        this.gedung = gedung;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNoKamar() {
        return noKamar;
    }

    public void setNoKamar(String noKamar) {
        this.noKamar = noKamar;
    }

    public Integer getLantai() {
        return lantai;
    }

    public void setLantai(Integer lantai) {
        this.lantai = lantai;
    }

    public Gedung getGedung() {
        return gedung;
    }

    public void setGedung(Gedung gedung) {
        this.gedung = gedung;
    }
}
