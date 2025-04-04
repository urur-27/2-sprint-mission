package com.sprint.mission.discodeit.exception.notfound;

import java.util.UUID;

public class ReadStatusNotFoundException extends RuntimeException {

  public ReadStatusNotFoundException(UUID readStatusId) {
    super("ReadStatus with id " + readStatusId + " not found");
  }
}