package com.sprint.mission.discodeit.dto2;

import com.sprint.mission.discodeit.entity.UserStatus;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        byte[] profileImage
) {}
