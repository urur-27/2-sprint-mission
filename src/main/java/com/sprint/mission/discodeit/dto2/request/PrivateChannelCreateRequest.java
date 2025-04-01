package com.sprint.mission.discodeit.dto2.request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> userIds
) { }
