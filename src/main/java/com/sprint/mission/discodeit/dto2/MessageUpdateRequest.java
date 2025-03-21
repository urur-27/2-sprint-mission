package com.sprint.mission.discodeit.dto2;

import java.util.UUID;

public record MessageUpdateRequest(
   UUID id,
   String content
) {}
