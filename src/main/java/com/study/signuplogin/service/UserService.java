package com.study.signuplogin.service;

import com.study.signuplogin.config.jwt.JwtUtil;
import com.study.signuplogin.dto.requestDto.SignupRequest;
import com.study.signuplogin.entity.User;
import com.study.signuplogin.entity.UserRoleEnum;
import com.study.signuplogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String ADMIN_TOKEN = "f679d89c320cc4adb72b7647a64ccbe520406dc3ee4578b44bcffbfa7ebbb85e30b964306b6398d3a2d7098ecd1bc203551e356ac5ec4a5ee0c7dc899fb704c5";

    public void signup(SignupRequest requestDto){
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword()); // 비밀번호 암호화

        // DB에 User 가 존재하는지 확인
        // isPresent() : Optional 객체에 값이 존재 여부 확인
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 사용자 권한 확인
        UserRoleEnum userRole = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 인증키가 틀려 등록이 불가능합니다.");
            }
            userRole = UserRoleEnum.ADMIN;
        }

        // 새로운 객체 생성
        User user = new User(username, password, userRole);   // role 추가

        // JPA : DB에 새로운 객체 저장
        userRepository.save(user);
    }
}
