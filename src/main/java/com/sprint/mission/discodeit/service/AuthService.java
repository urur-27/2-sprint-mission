package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;

public interface AuthService {

    UserResponse updateUserRole(RoleUpdateRequest request);
}