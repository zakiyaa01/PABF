package com.unand.asrama.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    public enum Role {
        Penghuni, Fasil, Admin
    }

    public enum JenisKelamin {
        L, P
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nim", length = 20, unique = true)
    private String nim;

    @Column(name = "nama", nullable = false, length = 100)
    private String nama;

    @Column(name = "asal", length = 100)
    private String asal;

    @Enumerated(EnumType.STRING)
    @Column(name = "jenis_kelamin", length = 1)
    private JenisKelamin jenisKelamin;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_kamar", nullable = true)
    private Kamar kamar;

    public User() {
    }

    public User(String nim, String nama, String asal, JenisKelamin jenisKelamin, String email, String password, Role role, Kamar kamar) {
        this.nim = nim;
        this.nama = nama;
        this.asal = asal;
        this.jenisKelamin = jenisKelamin;
        this.email = email;
        this.password = password;
        this.role = role;
        this.kamar = kamar;
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

    public String getAsal() {
        return asal;
    }

    public void setAsal(String asal) {
        this.asal = asal;
    }

    public JenisKelamin getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(JenisKelamin jenisKelamin) {
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Kamar getKamar() {
        return kamar;
    }

    public void setKamar(Kamar kamar) {
        this.kamar = kamar;
    }
}
