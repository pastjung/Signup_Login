package com.study.signuplogin.controller;

import com.study.signuplogin.config.jwt.JwtUtil;
import com.study.signuplogin.dto.requestDto.SignupRequest;
import com.study.signuplogin.dto.responseDto.CommonResponse;
import com.study.signuplogin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // ResponseBody + Controller
@RequestMapping("/users")    // URL 줄이기
@RequiredArgsConstructor
public class HomeController {
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 회원가입 : 인가(Authorization)
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestBody @Valid SignupRequest requestDto){
        try {
            userService.signup(requestDto);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(new CommonResponse("중복된 username 입니다.", HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED.value())
                .body(new CommonResponse("회원가입 성공", HttpStatus.CREATED.value()));
    }
}
