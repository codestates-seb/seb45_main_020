package com.pathfinder.server.global.exception.memberexception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberNotAgreeToTerms extends MemberException{
    public static final String MESSAGE = "회원가입 약관에 동의가 필요합니다.";
    public static final String CODE = "MEMBER-401";

    public MemberNotAgreeToTerms() {
        super(CODE, HttpStatus.UNAUTHORIZED, MESSAGE);
    }
}