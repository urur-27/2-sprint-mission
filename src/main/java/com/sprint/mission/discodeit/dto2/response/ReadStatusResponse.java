package com.sprint.mission.discodeit.dto2.response;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusResponse(
    UUID id,
    UUID userId,
    UUID channelId,
    Instant lastReadAt
) {

}
