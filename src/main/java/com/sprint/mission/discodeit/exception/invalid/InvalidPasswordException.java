package com.sprint.mission.discodeit.exception.invalid;

public class InvalidPasswordException extends RuntimeException {

  public InvalidPasswordException() {
    super("The password you entered does not match.");
  }
}
