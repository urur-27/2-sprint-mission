package com.sprint.mission.discodeit.dto2.response;

import java.time.Instant;
import java.util.UUID;

public record UserStatusResponse(
    UUID id,
    UUID userId,
    Instant lastActiveAt
) {

}
