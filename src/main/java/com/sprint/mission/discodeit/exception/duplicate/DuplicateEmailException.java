package com.sprint.mission.discodeit.exception.duplicate;

public class DuplicateEmailException extends RuntimeException {

  public DuplicateEmailException(String email) {
    super("User with email " + email + " already exists");
  }
}
