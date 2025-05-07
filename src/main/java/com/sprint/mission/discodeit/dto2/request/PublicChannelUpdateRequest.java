package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record PublicChannelUpdateRequest(
    @NotBlank(message = "New channel name must not be blank")
    String newName,

    @Nullable
    String newDescription
) {

}
