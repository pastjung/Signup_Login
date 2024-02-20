package com.study.signuplogin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.signuplogin.config.jwt.Filter.JwtAuthenticationFilter;
import com.study.signuplogin.config.jwt.Filter.JwtAuthorizationFilter;
import com.study.signuplogin.config.jwt.JwtUtil;
import com.study.signuplogin.config.jwt.UserDetails.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // Bean 사용 정의
@EnableWebSecurity  // Spring Security 사용 정의
@RequiredArgsConstructor    // 모든 필드를 가지는 생성자 생성
public class WebSecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    private final AuthenticationConfiguration authenticationConfiguration;


    // Password Encoder : 기존 Default 로 생성된 Password Encoder 대신 BCryptPasswordEncoder 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // JwtAuthorizationFilter 수동 주입
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService, objectMapper);
    }

    // securityFilterChain : 기존에 설정되어 있는 Spring Security 를 원하는대로 동작할 수 있도록 다룰수 있게 제공해주는 Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers("/").permitAll() // 메인 페이지 요청 허가
                        .requestMatchers("/users/**").permitAll() // 메인 페이지 요청 허가
                        .anyRequest().authenticated());     // 그 외 모든 요청 인증

        // 로그인 사용
        http.formLogin((formLogin) ->
                formLogin.loginPage("/login").permitAll()
        );

        // Filter 관리
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);  // JWT 검증 및 인가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);    // 로그인 및 JWT 생성

        return http.build();
    }

    // AuthenticationManager 수동 주입
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // JwtAuthenticationFilter 수동 주입
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }
}
