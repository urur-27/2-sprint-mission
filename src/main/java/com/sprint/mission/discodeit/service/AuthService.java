package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
public interface AuthService {
    UserResponse login(UserLoginRequest request);  // 로그인 기능 구현
}