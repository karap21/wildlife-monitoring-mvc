package com.wildlife_tracker.backend.util;

import java.security.MessageDigest;

public class PasswordUtil {

    // Fungsi mengubah teks biasa menjadi acakan kriptografi tingkat tinggi (SHA-256)
    public static String enkripsi(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengenkripsi password", e);
        }
    }
}