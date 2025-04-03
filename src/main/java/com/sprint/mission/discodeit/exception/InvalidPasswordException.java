package com.sprint.mission.discodeit.exception;

import java.util.UUID;

public class InvalidPasswordException extends RuntimeException {

  public InvalidPasswordException(String password) {
    super("The password you entered does not match. What you entered: " + password);
  }
}
