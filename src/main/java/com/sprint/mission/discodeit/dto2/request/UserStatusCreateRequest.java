package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(
    @NotNull(message = "User ID is required.")
    UUID userId,

    @NotNull(message = "Last accessed timestamp is required.")
    Instant lastAccessedAt
) {

}
