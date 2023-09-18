package com.pathfinder.server.global.exception.imageexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileIsNotImageFile extends ImageException{
    public static final String MESSAGE = "유효한 이미지 파일이 아닙니다.";
    public static final String CODE = "IMAGE-400";

    public FileIsNotImageFile() {
        super(CODE, HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
