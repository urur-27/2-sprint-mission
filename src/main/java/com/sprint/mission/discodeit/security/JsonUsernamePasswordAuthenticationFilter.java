package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Slf4j
@RequiredArgsConstructor
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // 경로 필터링
        setRequiresAuthenticationRequestMatcher(
            new AntPathRequestMatcher("/api/auth/login", "POST")
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("[LOGIN] attemptAuthentication 시작");
        try{
            // JSON 요청 body를 loginRequest로 변환
            UserLoginRequest loginReqeust = objectMapper.readValue(request.getInputStream(), UserLoginRequest.class);

            // username, password를 이용한 인증 토큰 객체 생성
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginReqeust.username(), loginReqeust.password()
            );

            // 토큰에 부가 정보 붙이고, 매니저에게 실제 인증 위임
            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            log.error("[LOGIN ERROR] Failed to parse request body: {}", e.getMessage(), e);
            throw new AuthenticationServiceException("Invalid login request format", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain,
        Authentication authResult) throws IOException, ServletException {
        this.getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException failed)
        throws IOException, ServletException {
        getFailureHandler().onAuthenticationFailure(request, response, failed);
    }
}
