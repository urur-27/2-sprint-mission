package com.sprint.mission.discodeit.dto2;

import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId,
        Instant lastAccessedAt
) {}
