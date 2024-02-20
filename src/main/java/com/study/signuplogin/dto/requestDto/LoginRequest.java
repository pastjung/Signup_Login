package com.study.signuplogin.dto.requestDto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class LoginRequest {
    @Pattern(regexp = "^[a-z0-9]{4,10}")
    private String username;

    @Pattern(regexp = "^[a-zA-Z0-9]{8,15}")
    private String password;
}
