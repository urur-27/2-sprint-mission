package com.sprint.mission.discodeit.exception.invalid;

public class InvalidJsonFormatException extends RuntimeException {

  public InvalidJsonFormatException(String message) {
    super(message);
  }

  public InvalidJsonFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}