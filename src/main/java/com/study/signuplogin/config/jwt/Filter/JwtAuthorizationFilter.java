package com.study.signuplogin.config.jwt.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.signuplogin.config.jwt.JwtUtil;
import com.study.signuplogin.config.jwt.UserDetails.UserDetailsServiceImpl;
import com.study.signuplogin.dto.responseDto.CommonResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // OncePerRequestFilter : 요청이 올 때 마다 Filter 를 거치도록 함

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JwtUtil 을 이용해 JWT 요청
        String JwtToken = jwtUtil.getJwtFromHeader(request);
        // JWT nullCheck
        if (Objects.nonNull(JwtToken)) {
            // JWT 검증
            if (jwtUtil.validateToken(JwtToken)) {
                // Claim 정보에 유저 정보 넣기
                Claims info = jwtUtil.getUserInfoFromToken(JwtToken);

                // 사용자 인증 정보 생성 및 인증 처리
                try {
                    setAuthentication(info.getSubject());
                } catch (Exception e) {
                    // 인증 처리 중 에러 발생시 로그 처리
                    log.error(e.getMessage());
                    return;
                }
            } else{
                // 인증 정보가 존재하지 않을 경우
                CommonResponse commonResponse = new CommonResponse("토큰이 유효하지 않습니다", HttpStatus.BAD_REQUEST.value());

                // response 의 body 설정
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 응답 값에 status 세팅
                response.setContentType("application/json:charset=UTF-8");  // Body 가 깨지지 않게 UTF-8로 설정
                response.getWriter().write(objectMapper.writeValueAsString(commonResponse));    // Body 부분에 생성한 responseDto 삽입
                // objectMapper : 응답 객체를 String 으로 변환
            }
        }
        // 다음 Filter 를 적용할 수 있도록 설정
        filterChain.doFilter(request, response);
    }

    // 인증 처리
    public void setAuthentication(String username) {
        // 현재 사용자의 보안 관련 정보를 저장한 변수 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // userDetails(현재 사용자의 인증 정보) 를 Authentication 의 principal 로 설정
        Authentication authentication = createAuthentication(username);

        // userDetails 를 SecurityContext 에 담기
        context.setAuthentication(authentication);
        // userDetails 를 현재 실행중인 스레드의 SecurityContext 로 설정 : 이제 @AuthenticationPrincipal 로 User 조회 가능
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        // 사용자명(username) 을 기반으로 사용자(user) 조회 -> user 를 userDetails 에 담기
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}