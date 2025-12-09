package com.tradetrackpro.dto;

import lombok.Data;

@Data
public class SecurityQuestionRequest {
    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
