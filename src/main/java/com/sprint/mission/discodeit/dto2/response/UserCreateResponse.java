package com.sprint.mission.discodeit.dto2.response;

import java.util.UUID;

public record UserCreateResponse(
        UUID uuid,
        String username,
        String email
) {}