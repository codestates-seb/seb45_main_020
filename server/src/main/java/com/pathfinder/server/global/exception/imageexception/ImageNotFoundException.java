package com.pathfinder.server.global.exception.imageexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ImageNotFoundException extends ImageException{
    public static final String MESSAGE = "존재하지 않는 이미지입니다.";
    public static final String CODE = "IMAGE-404";

    public ImageNotFoundException() {
        super(CODE, HttpStatus.NOT_FOUND, MESSAGE);
    }
}
