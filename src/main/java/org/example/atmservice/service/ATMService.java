package org.example.atmservice.service;

import lombok.RequiredArgsConstructor;
import org.example.atmservice.dto.AccountRequest;
import org.example.atmservice.dto.AuthRequest;
import org.example.atmservice.dto.AuthResponse;
import org.example.atmservice.model.ATM;
import org.example.atmservice.repository.ATMRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

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
    private LocalDateTime expired_at = null;
    private String token;

    // Login and save ATM session info
    public AuthResponse login(AuthRequest authRequest) {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(authUrl + "/login", authRequest, AuthResponse.class);
        ATM atm = new ATM();
        atmRepository.save(atm);
        atmId = atm.getId();
        cardNumber = authRequest.getCardNumber();
        token = response.getBody().getRefreshToken();
        expired_at = LocalDateTime.now().plusMinutes(5);
        return response.getBody();
    }

    // Deposit money to account
    public void deposit(AccountRequest request) {
        if (atmId != null && cardNumber != null && expiresCheck()) {
            request.setAtmId(atmId);
            request.setCardNumber(cardNumber);
            request.setToken(token);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            HttpEntity<AccountRequest> entity = new HttpEntity<>(request,httpHeaders);
            restTemplate.postForEntity(accountUrl + "/deposit", entity, AccountRequest.class);
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Withdraw money from account
    public void withdraw(AccountRequest accountRequest) {
        if (atmId != null && cardNumber != null && expiresCheck()) {
            accountRequest.setAtmId(atmId);
            accountRequest.setCardNumber(cardNumber);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            HttpEntity<AccountRequest> entity = new HttpEntity<>(accountRequest,httpHeaders);
            restTemplate.postForEntity(accountUrl + "/withdraw", entity, AccountRequest.class);
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Check account balance
    public String balance() {
        if (cardNumber != null && expiresCheck()) {
            AccountRequest request = new AccountRequest();
            request.setCardNumber(cardNumber);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(token);
            HttpEntity<AccountRequest> entity = new HttpEntity<>(request,httpHeaders);
            ResponseEntity<Double> response = restTemplate.postForEntity(accountUrl + "/balance", entity, Double.class);
            return "Balance: " + response.getBody();
        } else {
            throw new RuntimeException("Login please");
        }
    }

    // Logout and clear session
    public void logout(AuthRequest authRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(token);
        HttpEntity<AuthRequest> entity = new HttpEntity<>(authRequest,httpHeaders);
        restTemplate.postForEntity(authUrl + "/logout", entity, AuthRequest.class);
        cardNumber = null;
        expired_at = null;
    }

    private boolean expiresCheck(){
        if(expired_at.isBefore(LocalDateTime.now())){
            throw new RuntimeException("Time expires login try again");
        }
        expired_at = LocalDateTime.now().plusMinutes(5);
        return true;
    }
}
