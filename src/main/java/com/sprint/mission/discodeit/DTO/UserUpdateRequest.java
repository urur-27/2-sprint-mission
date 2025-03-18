package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserUpdateRequest(
        UUID userId,
        String username,
        String email,
        String password,
        byte[] profileImage // 선택적 필드
) {}