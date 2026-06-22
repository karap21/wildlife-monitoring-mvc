package com.wildlife_tracker.backend.model;

public abstract class AkunPegawai {
    protected Long id;
    protected String namaLengkap;
    protected String email;
    protected String role;

    public AkunPegawai(Long id, String namaLengkap, String email, String role) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public abstract String getHalamanAwal();
}