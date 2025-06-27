package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JsonLogoutFilter extends OncePerRequestFilter {

    private static final String LOGOUT_URI = "/api/auth/logout";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        if (!isLogoutRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 무효화
            log.info("Session invalidated for logout");
        }

        // CSRF 및 JSESSIONID 쿠키 삭제
        Cookie csrfCookie = new Cookie("CSRF-TOKEN", "");
        csrfCookie.setMaxAge(0);
        csrfCookie.setPath("/");
        response.addCookie(csrfCookie);

        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setMaxAge(0);
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);

        SecurityContextHolder.clearContext(); // SecurityContext 초기화
        log.info("SecurityContext cleared for logout");

        response.setStatus(HttpServletResponse.SC_OK);
    }

    private boolean isLogoutRequest(HttpServletRequest request) {
        return "POST".equalsIgnoreCase(request.getMethod())
            && LOGOUT_URI.equals(request.getRequestURI());
    }
}
