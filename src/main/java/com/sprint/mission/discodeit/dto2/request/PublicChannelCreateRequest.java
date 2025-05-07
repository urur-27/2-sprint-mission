package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.lang.Nullable;

public record PublicChannelCreateRequest(
    @NotBlank(message = "Channel name must not be blank")
    String name,

    @Nullable
    String description
) {

}
