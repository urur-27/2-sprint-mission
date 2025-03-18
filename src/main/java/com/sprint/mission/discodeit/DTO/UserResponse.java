package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        boolean isOnline // 사용자의 온라인 상태 정보 추가
) {}