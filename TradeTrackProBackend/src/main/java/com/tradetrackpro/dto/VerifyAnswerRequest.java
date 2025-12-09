package com.tradetrackpro.dto;

import lombok.Data;

@Data
public class VerifyAnswerRequest {
    private String username;
    private String answer;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}
