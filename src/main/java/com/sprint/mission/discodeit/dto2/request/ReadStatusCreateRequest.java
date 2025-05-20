package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record ReadStatusCreateRequest(
    @NotNull(message = "User ID is required.")
    UUID userId,

    @NotNull(message = "Channel ID is required.")
    UUID channelId,

    @NotNull(message = "Last read timestamp is required.")
    Instant lastReadAt
) {

}
