package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;
  private final UserMapper userMapper;

  @PostMapping("/login")
  public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    return ResponseEntity.ok(userMapper.toResponse(user));
  }
}