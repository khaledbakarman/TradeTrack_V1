package com.tradetrackpro.dto;

import lombok.Data;

@Data
public class ChangeSecurityQuestionRequest {
    private String securityQuestion;
    private String securityAnswer;
}
