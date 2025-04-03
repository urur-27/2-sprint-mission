package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class BinaryContentNotFoundException extends RuntimeException {

  public BinaryContentNotFoundException(UUID binaryContentId) {
    super("BinaryContent with ID " + binaryContentId + " not found");
  }
}
