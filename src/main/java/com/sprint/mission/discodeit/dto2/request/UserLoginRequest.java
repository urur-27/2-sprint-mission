package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequest(
    @NotBlank(message = "Username is required.")
    @Size(max = 50, message = "Username must be at most 50 characters.")
    String username,

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 60, message = "Password must be between 8 and 60 characters.")
    String password
) {

}