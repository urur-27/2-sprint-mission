package com.sprint.mission.discodeit.dto2.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
    @JsonProperty("participantIds")
    List<UUID> userIds
) {

}
