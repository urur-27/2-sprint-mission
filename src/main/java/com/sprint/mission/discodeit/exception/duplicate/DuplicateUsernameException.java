package com.sprint.mission.discodeit.exception.duplicate;

public class DuplicateUsernameException extends RuntimeException {

  public DuplicateUsernameException(String username) {
    super("User with username " + username + " already exists");
  }
}