package com.study.signuplogin.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank
    @Pattern(regexp = "^[a-z0-9]{4,10}",
            message = "아이디는 최소 4자 이상, 10자 이하로 알파벳 소문자와 숫자로 구성")
    private String username;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{8,15}",
            message = "비밀번호는 최소 7자 이상, 15자 이하로 알파벳과 특수문자, 숫자로 구성")
    private String password;

    private boolean admin = false;  // 관리자 여부

    private String adminToken = ""; // 관리자 토큰 저장소
}