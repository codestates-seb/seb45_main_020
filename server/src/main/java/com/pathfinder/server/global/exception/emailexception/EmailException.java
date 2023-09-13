package com.pathfinder.server.global.exception.emailexception;

import com.pathfinder.server.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public abstract class EmailException extends BusinessException {

    protected EmailException(String errorCode, HttpStatus httpStatus, String message) {
        super(errorCode, httpStatus, message);
    }
}

