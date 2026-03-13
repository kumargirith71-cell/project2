package com.example.Profenaa_touch.dto;

public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String type;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.type = "Bearer";
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getType() {
        return type;
    }
}
