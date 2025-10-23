package org.example.atmservice.service;

import lombok.RequiredArgsConstructor;
import org.example.atmservice.dto.AccountRequest;
import org.example.atmservice.dto.AuthRequest;
import org.example.atmservice.dto.AuthResponse;
import org.example.atmservice.model.ATM;
import org.example.atmservice.repository.ATMRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Automated Teller Machine operations
@Service
@RequiredArgsConstructor
public class ATMService {

    private final RestTemplate restTemplate;
    private final ATMRepository atmRepository;

    @Value("${auth.url}")
    private String authUrl;

    @Value("${account.url}")
    private String accountUrl;

    private Long atmId;
    private Long cardNumber = null;

    // Login and save ATM session info
    public AuthResponse login(AuthRequest authRequest) {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(authUrl + "/login", authRequest, AuthResponse.class);
        ATM atm = new ATM();
        atmRepository.save(atm);
        atmId = atm.getId();
        cardNumber = authRequest.getCardNumber();
        return response.getBody();
    }

    // Deposit money to account
    public void deposit(AccountRequest request) {
        if (atmId != null && cardNumber != null) {
            request.setAtmId(atmId);
            request.setCardNumber(cardNumber);
            restTemplate.postForEntity(accountUrl + "/deposit", request, AccountRequest.class);
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Withdraw money from account
    public void withdraw(AccountRequest accountRequest) {
        if (atmId != null && cardNumber != null) {
            accountRequest.setAtmId(atmId);
            accountRequest.setCardNumber(cardNumber);
            restTemplate.postForEntity(accountUrl + "/withdraw", accountRequest, AccountRequest.class);
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Check account balance
    public String balance() {
        if (cardNumber != null) {
            AccountRequest request = new AccountRequest();
            request.setCardNumber(cardNumber);
            ResponseEntity<Double> response = restTemplate.postForEntity(accountUrl + "/balance", request, Double.class);
            return "Balance: " + response.getBody();
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Logout and clear session
    public void logout(AuthRequest authRequest) {
        restTemplate.postForEntity(authUrl + "/logout", authRequest, AuthRequest.class);
        cardNumber = null;
    }
}
