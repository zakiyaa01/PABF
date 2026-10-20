package com.unand.asrama.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Email, NIM, atau NIP wajib diisi")
    private String identifier; // Email, NIM (Penghuni), or NIP (Fasil/Admin)

    private String password;

    private String deviceId; // Unique Android ID / IMEI for single-device binding

    private Boolean isBiometric = false; // Flag for BiometricPrompt login

    public LoginRequest() {
    }

    public LoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getIsBiometric() {
        return isBiometric;
    }

    public void setIsBiometric(Boolean isBiometric) {
        this.isBiometric = isBiometric;
    }
}
