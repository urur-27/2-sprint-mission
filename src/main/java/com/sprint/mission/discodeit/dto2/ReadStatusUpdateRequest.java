package com.sprint.mission.discodeit.dto2;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest(
        UUID readStatusId,
        UUID userId,
        UUID channelId,
        Instant lastReadAt
) {}
