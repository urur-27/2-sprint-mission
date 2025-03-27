package com.sprint.mission.discodeit.dto2.request;

public record UserLoginRequest(
        String username,
        String password
) {}