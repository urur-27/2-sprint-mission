package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.JwtSession;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserMapper userMapper;
    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${jwt.refresh-token-expiry}")
    private long refreshTokenExpiryMillis;

    @GetMapping("/me")
    public ResponseEntity<String> getAccessTokenByRefreshToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing refresh token");
        }

        String accessToken = authService.getAccessTokenByRefreshToken(refreshToken);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        return ResponseEntity.ok(accessToken);
    }

    @PutMapping("/role")
    public ResponseEntity<UserDto> updateUserRole(@RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(authService.updateUserRole(request));
    }

    // 리프레시 토큰을 이용한 액세스 토큰 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response) {
        // 토큰이 비어있는지 체크
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DiscodeitException(ErrorCode.UNAUTHORIZED_USER));
        }

        Optional<JwtSession> newSessionOpt = jwtService.rotateRefreshToken(refreshToken);

        if (newSessionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DiscodeitException(ErrorCode.UNAUTHORIZED_USER));
        }

        JwtSession newSession = newSessionOpt.get();

        ResponseCookie cookie = ResponseCookie.from("refresh_token", newSession.getRefreshToken())
                .path("/")
//                .httpOnly(true) // 임시 생략
//                .secure(true) // 임시 생략
                .sameSite("Lax")
                .maxAge(refreshTokenExpiryMillis / 1000)
                .build();
        response.setHeader("Set-Cookie", cookie.toString());

        // 응답 바디에 액세스 토큰만 반환 (문자열)
        return ResponseEntity.ok(newSession.getAccessToken());
    }

}