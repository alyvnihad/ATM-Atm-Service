package org.example.atmservice.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private Long cardNumber;
    private String refreshToken;
    private String pin;
}
