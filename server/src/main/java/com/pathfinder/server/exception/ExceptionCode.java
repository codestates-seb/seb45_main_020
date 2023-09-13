package com.pathfinder.server.exception;

import lombok.Getter;

public enum ExceptionCode {
    // User 관련 예외
    USER_NOT_FOUND(404, "User not found"),
    EMAIL_EXISTS(409, "Email already exists"),
    NAME_EXISTS(409, "Name already exists"),
    USER_CREDENTIALS_INVALID(401, "Invalid user credentials"),

    // Diary 관련 예외
    DIARY_NOT_FOUND(404, "Diary not found"),
    DIARY_EDIT_UNAUTHORIZED(403, "Unauthorized Diary edit"),
    DIARY_DELETE_UNAUTHORIZED(403, "Unauthorized Diary edit"),

    // Reward 관련 예외
    REWARD_NOT_FOUND(404, "Reward not found"),
    REWARD_NOT_UNLOCK(403, "Reward not unlock"),

    // General 예외
    INTERNAL_SERVER_ERROR(500, "Internal server error"),
    INVALID_REQUEST(400, "Invalid request");


    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}