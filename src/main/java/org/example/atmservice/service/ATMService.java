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

    public AuthResponse login(AuthRequest authRequest) {
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(authUrl + "/login", authRequest, AuthResponse.class);
        ATM atm = new ATM();
        atmRepository.save(atm);
        atmId = atm.getId();
        cardNumber = authRequest.getCardNumber();
        return response.getBody();
    }

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

    public void logout(AuthRequest authRequest) {
        restTemplate.postForEntity(authUrl + "/logout", authRequest, AuthRequest.class);
        cardNumber = null;
    }
}
