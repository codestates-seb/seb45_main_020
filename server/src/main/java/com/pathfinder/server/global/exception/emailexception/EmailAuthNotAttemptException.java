package com.pathfinder.server.global.exception.emailexception;

import org.springframework.http.HttpStatus;

public class EmailAuthNotAttemptException extends EmailException {

    public static final String MESSAGE = "이메일 인증을 먼저 시도해주세요.";
    public static final String CODE = "EMAIL-400";

    public EmailAuthNotAttemptException() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
