package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MessageCreateRequest(
    @NotBlank(message = "Message content is required.")
    String content,

    @NotNull(message = "Author ID is required.")
    UUID authorId,

    @NotNull(message = "Channel ID is required.")
    UUID channelId
) {

}
