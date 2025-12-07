package com.tradetrackpro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AuthResponse {
    private Long userId;
    private String message;

    public AuthResponse() {}

    public AuthResponse(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
