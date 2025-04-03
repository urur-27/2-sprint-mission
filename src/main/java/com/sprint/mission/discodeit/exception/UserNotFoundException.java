package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {

  public UserNotFoundException(UUID userId) {
    super("User with ID " + userId + " not found");
  }

  public UserNotFoundException(String userName) {
    super("User with name " + userName + " not found");
  }
}
