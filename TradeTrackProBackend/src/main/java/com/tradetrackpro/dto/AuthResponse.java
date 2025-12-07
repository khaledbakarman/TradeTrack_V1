package com.tradetrackpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthResponse {
    private Long userId;
    private String username;
    private String token;
    private String message;

    public AuthResponse() {}

    public AuthResponse(Long userId, String username, String token, String message) {
        this.userId = userId;
        this.username = username;
        this.token = token;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
