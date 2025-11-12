package org.example.atmservice.exception;

import org.springframework.http.HttpStatus;

public class ExpiredTimeException extends ControllerException {
    public ExpiredTimeException(String message) {
        super(message, HttpStatus.REQUEST_TIMEOUT);
    }
}
