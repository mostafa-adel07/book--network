package com.example.book_network.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter

public enum BusinessErrorCodes {
    NO_CODE(0,HttpStatus.NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, HttpStatus.BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, HttpStatus.BAD_REQUEST, "New password does not match"),
    ACCOUNT_DISABLED(303, HttpStatus.FORBIDDEN, "Account is disabled"),
    ACCOUNT_LOCKED(302, HttpStatus.FORBIDDEN, "User account is locked"),
    BAD_CREDENTIALS(304, HttpStatus.UNAUTHORIZED, "Login email or password is incorrect"),

    ;


    private final int code;
    private final String description;
    private final HttpStatus httpStatus;


    BusinessErrorCodes(int code, HttpStatus httpStatus ,String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
