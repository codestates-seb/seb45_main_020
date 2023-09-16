package com.pathfinder.server.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class MemberDto {
    @Getter
    @AllArgsConstructor
    public static class Post {
        @NotNull
        @Email
        private String email;
        @NotNull
        private String name;
        @NotNull
        @Size(min = 8)
        private String password;
        @NotNull
        private Boolean agreeToTerms;
    }

    @Getter
    @AllArgsConstructor
    public static class Patch {
        private long memberId;
        private String name;
        private String password;
        // todo 패스워드 vaild추가
        private String introduce;
        private String profileImageUrl;

        public void setMemberId(long memberId) {
            this.memberId = memberId;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String name;
        private String email;
        private String introduce;
        private String profileImageUrl;
    }
}
