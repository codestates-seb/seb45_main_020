package com.pathfinder.server.global.exception.memberexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberNotAgreeToTerms extends MemberException{
    public static final String MESSAGE = "약관에 동의하지 않은 사용자입니다.";
    public static final String CODE = "MEMBER-401";

    public MemberNotAgreeToTerms() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}

