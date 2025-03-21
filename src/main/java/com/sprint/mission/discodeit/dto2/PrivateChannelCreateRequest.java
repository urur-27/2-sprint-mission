package com.sprint.mission.discodeit.dto2;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> userIds
) { }
