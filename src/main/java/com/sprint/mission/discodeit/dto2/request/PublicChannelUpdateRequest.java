package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "New channel name must not be blank.")
    @Size(max = 100, message = "New channel name must be at most 100 characters.")
    String newName,

    @Nullable
    @Size(max = 500, message = "New channel description must be at most 500 characters.")
    String newDescription
) {

}
