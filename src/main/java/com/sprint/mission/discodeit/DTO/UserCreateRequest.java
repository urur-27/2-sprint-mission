package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.UserStatus;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        byte[] profileImage,
        UserStatus status
) {}
