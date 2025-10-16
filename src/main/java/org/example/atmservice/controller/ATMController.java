package org.example.atmservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.atmservice.dto.AuthRequest;
import org.example.atmservice.dto.AuthResponse;
import org.example.atmservice.service.ATMService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/atm")
@RequiredArgsConstructor
public class ATMController {

    private final ATMService atmService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest){
        return atmService.login(authRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody AuthRequest authRequest){
        atmService.logout(authRequest);
        return ResponseEntity.ok("Log out Successfully");
    }
}
