package com.sprint.mission.discodeit.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class CustomCsrfDebugFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        CsrfToken token = tokenRepository.loadToken(request);

        String headerToken = null;
        if (token != null) {
            headerToken = request.getHeader(token.getHeaderName());
        }

        log.info("[CSRF DEBUG] Method: {}, URI: {}", request.getMethod(), request.getRequestURI());
        log.info("[CSRF DEBUG] Token from Cookie: {}", token != null ? token.getToken() : "null");
        log.info("[CSRF DEBUG] Token from Header: {}", headerToken != null ? headerToken : "null");

        filterChain.doFilter(request, response);
    }
}
