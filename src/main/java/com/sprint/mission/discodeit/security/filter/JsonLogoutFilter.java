package com.sprint.mission.discodeit.security.filter;

import com.sprint.mission.discodeit.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JsonLogoutFilter extends OncePerRequestFilter {

    private static final String LOGOUT_URI = "/api/auth/logout";
    private final JwtService jwtService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키에서 refresh_token 추출
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (refreshToken != null) {
            // JwtService를 통해 리프레시 토큰 무효화
            jwtService.invalidateSession(refreshToken);
            log.info("Refresh token invalidated: {}", refreshToken);

            // 쿠키 삭제 (클라이언트의 refresh_token 쿠키 만료)
            Cookie cookie = new Cookie("refresh_token", null);
            cookie.setPath("/");
//            cookie.setHttpOnly(true);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } else {
            log.warn("No refresh token found in cookie during logout.");
        }

        SecurityContextHolder.clearContext(); // SecurityContext 초기화
        log.info("SecurityContext cleared for logout");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
            && LOGOUT_URI.equals(request.getRequestURI());
    }

    // 쿠키에서 토큰을 추출하는 메서드
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
