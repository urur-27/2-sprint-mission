package com.sprint.mission.discodeit.dto2.response;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    BinaryContentResponse profile,
    Boolean online
) {

}