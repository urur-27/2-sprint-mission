package com.sprint.mission.discodeit.exception;

import java.time.Instant;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

  private final Instant timestamp = Instant.now();
  private final int status;
  private final String error;
  private final String message;
  private final String path;

  public ErrorResponse(HttpStatus status, String message, String path) {
    this.status = status.value();
    this.error = status.getReasonPhrase();
    this.message = message;
    this.path = path;
  }

}
