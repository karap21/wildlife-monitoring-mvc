package com.wildlife_tracker.backend.model;

public class Peneliti extends AkunPegawai {
    public Peneliti(Long id, String namaLengkap, String email) {
        super(id, namaLengkap, email, "PENELITI");
    }

    @Override
    public String getHalamanAwal() {
        return "redirect:/";
    }
}