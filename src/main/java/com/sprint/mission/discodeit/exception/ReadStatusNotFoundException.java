package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class ReadStatusNotFoundException extends RuntimeException {

  public ReadStatusNotFoundException(UUID readStatusId) {
    super("ReadStatus with id " + readStatusId + " not found");
  }
}