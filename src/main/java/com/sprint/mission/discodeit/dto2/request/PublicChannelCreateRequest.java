package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record PublicChannelCreateRequest(
    @NotBlank(message = "Channel name must not be blank.")
    @Size(max = 100, message = "Channel name must be at most 100 characters.")
    String name,

    @Nullable
    @Size(max = 500, message = "Channel description must be at most 500 characters.")
    String description
) {

}
