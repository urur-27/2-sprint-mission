package com.sprint.mission.discodeit.dto2;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelUpdateRequest(
        UUID id,
        ChannelType type,
        String name,
        String description
) {}
