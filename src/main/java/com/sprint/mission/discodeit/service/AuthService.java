package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {

  User login(UserLoginRequest request);  // 로그인 기능 구현
}