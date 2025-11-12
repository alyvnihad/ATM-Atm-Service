package org.example.atmservice.exception;

import org.springframework.http.HttpStatus;

public class LoginException extends ControllerException {
    public LoginException(String message) {
        super(message,HttpStatus.UNAUTHORIZED);
    }
}
