package com.pathfinder.server.global.exception.emailexception;

import org.springframework.http.HttpStatus;

public class EmailAuthNotCompleteException extends EmailException {

    public static final String MESSAGE = "이메일 인증이 완료되지 않았습니다.";
    public static final String CODE = "EMAIL-401";

    public EmailAuthNotCompleteException() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}
