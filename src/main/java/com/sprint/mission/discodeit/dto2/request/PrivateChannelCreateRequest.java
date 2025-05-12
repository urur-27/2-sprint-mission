package com.sprint.mission.discodeit.dto2.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @JsonProperty("participantIds")
    @NotEmpty(message = "Participant IDs must not be empty")
    List<@NotNull(message = "Each participant ID is required.") UUID> userIds
) {

}
