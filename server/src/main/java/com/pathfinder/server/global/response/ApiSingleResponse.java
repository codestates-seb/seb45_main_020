package com.pathfinder.server.global.response;

import com.pathfinder.server.global.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiSingleResponse<T> {

    private T data;
    private int code;
    private String status;
    private String message;

    public static ApiSingleResponse<Void> fail(BusinessException exception) {
        return new ApiSingleResponse<>(
                null,
                exception.getHttpStatus().value(),
                exception.getErrorCode(),
                exception.getMessage());
    }

    public static <T> ApiSingleResponse<T> ok(T data) {
        return ApiSingleResponse.of(data, HttpStatus.OK);
    }

    public static <T> ApiSingleResponse<T> of(T data, HttpStatus httpStatus) {
        return ApiSingleResponse.of(data, httpStatus, httpStatus.getReasonPhrase());
    }

    public static <T> ApiSingleResponse<T> of(T data, HttpStatus httpStatus, String message) {
        return new ApiSingleResponse<>(
                data,
                httpStatus.value(),
                httpStatus.name(),
                message);
    }
}
