package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.util.LogUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;


  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@Valid @RequestBody UserLoginRequest loginRequest) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[LOGIN] status=START, username={}, traceId={}",
        log.isDebugEnabled() ? loginRequest.username() : LogUtils.mask(loginRequest.username()),
        traceId);

    User user = authService.login(loginRequest);
    // 로그인 시 항상 온라인 상태로 처리
    UserResponse response = userMapper.toResponse(user, true);

    // 성공 로그
    log.info("[LOGIN] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(user.getId()), traceId);

    return ResponseEntity.ok(response);
  }
}