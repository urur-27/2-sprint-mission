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
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final UserMapper userMapper;
    private final CsrfTokenRepository csrfTokenRepository;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMillis;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {

        log.debug("authentication principal class: {}", authentication.getPrincipal().getClass());

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser(); // 실제 엔티티 꺼내기
        UserDto userDto = userMapper.toDto(user);

        // JwtSession 생성 (DB 저장 & 액세스 토큰, 리프레시 토큰 생성)
        JwtSession jwtSession = jwtService.createSession(userDto);

        // 액세스 토큰을 응답 바디에 문자열로 반환
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(jwtSession.getAccessToken());

        // 리프레시 토큰을 쿠키로 반환
        String refreshToken = jwtSession.getRefreshToken();
        int maxAge = (int) (refreshTokenExpiryMillis / 1000);
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
