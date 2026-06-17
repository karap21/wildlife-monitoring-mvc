package com.wildlife_tracker.backend.service.impl;

import com.wildlife_tracker.backend.repository.AkunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AkunService {

    private final AkunRepository akunRepository;

    public Map<String, Object> prosesLogin(String email, String password) {
        return akunRepository.validasiLogin(email, password);
    }
}