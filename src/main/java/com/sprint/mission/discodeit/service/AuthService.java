package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserLoginRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
public interface AuthService {
    UserResponse login(UserLoginRequest request);  // 로그인 기능 구현
}