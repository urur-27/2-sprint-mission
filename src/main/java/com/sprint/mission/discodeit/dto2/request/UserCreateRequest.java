package com.sprint.mission.discodeit.dto2.request;

public record UserCreateRequest(
        String username,
        String email,
        String password,
        byte[] profileImage
) {}
