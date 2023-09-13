package com.pathfinder.server.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberMailConfirmApiRequest {

    @Email(message = "이메일을 정확하게 입력해주세요")
    @NotNull(message = "이메일을 입력해주세요")
    private String email;
    @NotBlank(message = "코드를 정확히 입력해주세요")
    private String code;
}
