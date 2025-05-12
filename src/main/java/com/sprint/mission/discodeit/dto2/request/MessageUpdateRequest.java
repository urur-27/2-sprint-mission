package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;

public record MessageUpdateRequest(
    @NotBlank(message = "New message content is required.")
    String newContent
) {

}
