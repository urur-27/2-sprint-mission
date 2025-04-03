package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class DuplicateReadStatusException extends RuntimeException {

  public DuplicateReadStatusException(UUID userId, UUID channelId) {
    super("ReadStatus with userId " + userId + " and channelId " + channelId + " already exists");
  }
}