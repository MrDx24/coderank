package com.coderank.apigateway.ApiGateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenValidationException extends RuntimeException {
    private final String errorMessage;
    private final HttpStatus httpStatus;

    public TokenValidationException(String errorMessage, HttpStatus httpStatus) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.httpStatus = httpStatus;
    }

}

