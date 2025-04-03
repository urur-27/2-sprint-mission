package com.sprint.mission.discodeit.dto2.request;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
    String content,
    UUID authorId,
    UUID channelId
) {

}
