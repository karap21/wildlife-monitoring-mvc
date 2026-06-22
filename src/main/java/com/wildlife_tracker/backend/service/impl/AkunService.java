package com.wildlife_tracker.backend.service.impl;

import com.wildlife_tracker.backend.model.AkunPegawai;
import com.wildlife_tracker.backend.repository.AkunRepository;
import com.wildlife_tracker.backend.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AkunService {

    private final AkunRepository akunRepository;

    public AkunPegawai prosesLogin(String email, String password) {
        // Enkripsi password ketikan user sebelum dicocokkan ke database
        String passwordEnkripsi = PasswordUtil.enkripsi(password);
        return akunRepository.validasiLogin(email, passwordEnkripsi);
    }
}