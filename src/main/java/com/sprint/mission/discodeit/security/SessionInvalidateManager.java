package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.JwtSessionRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionInvalidateManager {

    private final JwtSessionRepository jwtSessionRepository;

    // userId에 해당하는 모든 세션 삭제 (강제 로그아웃)
    public void invalidateIfPresent(UUID userId) {
        jwtSessionRepository.deleteAllByUserId(userId);
    }
}
