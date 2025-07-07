package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Profile("local")
public class CustomCsrfDebugFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository tokenRepository;

    public CustomCsrfDebugFilter(CsrfTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        CsrfToken token = tokenRepository.loadToken(request);

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("CSRF-TOKEN".equals(cookie.getName())) {
                    log.info("[DEBUG] Raw cookie token: {}", cookie.getValue());
                }
            }
        }

        // 헤더에서 가져온 X-Csrf-Token 값 확인
        String headerToken = request.getHeader(token != null ? token.getHeaderName() : "X-CSRF-TOKEN");

        log.info("[CSRF DEBUG] Method: {}, URI: {}", request.getMethod(), request.getRequestURI());
        log.info("[CSRF DEBUG] Token from Cookie Repository: {}", token != null ? token.getToken() : "null");
        log.info("[CSRF DEBUG] Token from Header: {}", headerToken);

        filterChain.doFilter(request, response);
    }
}
