package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record UserStatusUpdateRequest(
    @NotNull(message = "New last active timestamp is required.")
    Instant newLastActiveAt
) {

}
