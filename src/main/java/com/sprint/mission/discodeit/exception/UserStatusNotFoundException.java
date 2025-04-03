package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserStatusNotFoundException extends RuntimeException {

  public UserStatusNotFoundException(UUID userStatusId) {
    super("UserStatus with ID " + userStatusId + " not found");
  }
}