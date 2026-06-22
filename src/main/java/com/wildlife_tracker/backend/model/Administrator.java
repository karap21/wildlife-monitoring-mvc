package com.wildlife_tracker.backend.model;

public class Administrator extends AkunPegawai {
    public Administrator(Long id, String namaLengkap, String email) {
        super(id, namaLengkap, email, "ADMINISTRATOR");
    }

    @Override
    public String getHalamanAwal() {
        return "redirect:/";
    }
}