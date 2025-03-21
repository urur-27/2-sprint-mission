package com.sprint.mission.discodeit.dto2;

import java.time.Instant;
import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID id,
        Instant lastAccessedAt
) {}
