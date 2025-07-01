package com.sprint.mission.discodeit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JsonLogoutFilter extends OncePerRequestFilter {

    private static final String LOGOUT_URI = "/api/auth/logout";
    private final PersistentTokenBasedRememberMeServices rememberMeServices;


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

        // Remember-Me 쿠키 삭제
        Cookie cookie = new Cookie("remember-me-cookie", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);
        log.info("Remember-Me cookie deleted");

        // Remember-Me DB 토큰 삭제
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("auth = {}", auth);

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            rememberMeServices.logout(request, response, auth);
            log.info("Remember-Me DB token deleted for user: {}", username);
        } else {
            log.warn("Authentication not found or invalid during logout.");
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
