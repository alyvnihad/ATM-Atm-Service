package org.example.atmservice.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private Long atmId;
    private Long cardNumber;
    private Double amount;
}
