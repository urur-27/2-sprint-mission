package com.sprint.mission.discodeit.DTO;

public record UserLoginRequest(
        String username,
        String password
) {}