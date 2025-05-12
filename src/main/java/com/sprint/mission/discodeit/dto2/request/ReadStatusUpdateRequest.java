package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record ReadStatusUpdateRequest(
    @NotNull(message = "New last read timestamp is required.")
    Instant newLastReadAt
) {

}
