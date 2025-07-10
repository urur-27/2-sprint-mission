package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.JwtSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserMapper userMapper;
    private final CsrfTokenRepository csrfTokenRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final long refreshTokenExpiryMillis;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        // SecurityContext에 인증 정보 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        // 세션에도 저장 (세션 기반 인증 유지)
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);


        log.debug("authentication principal class: {}", authentication.getPrincipal().getClass());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser(); // 실제 엔티티 꺼내기
        UserDto userDto = userMapper.toDto(user);

        // JwtSession 생성 (DB 저장 & 액세스 토큰, 리프레시 토큰 생성)
        JwtSession jwtSession = jwtService.createSession(userDto);

        String accessToken = jwtSession.getAccessToken();
        log.debug("[LOGIN] accessToken = {}", accessToken);

        // 액세스 토큰을 응답 바디에 문자열로 반환
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(accessToken);

        // 리프레시 토큰을 쿠키로 반환
        String refreshToken = jwtSession.getRefreshToken();
        int maxAge = (int) (refreshTokenExpiryMillis / 1000);
        log.info("[쿠키 maxAge 확인] refreshTokenExpiryMillis: {}, maxAge: {}", refreshTokenExpiryMillis, maxAge);
        StringBuilder cookieStr = new StringBuilder();
        cookieStr.append("refresh_token=").append(refreshToken)
                .append("; Path=/")
                .append("; Max-Age=").append(maxAge)
                .append("; SameSite=Lax");
        if (request.isSecure()) {
            cookieStr.append("; Secure");
        }
        response.addHeader("Set-Cookie", cookieStr.toString());

        // 새로운 CSRF 토큰 생성 및 쿠키에 반영
        CsrfToken newToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(newToken, request, response);
    }
}
