package com.sprint.mission.discodeit.exception.notfound;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {

  public MessageNotFoundException(UUID messageId) {
    super("Message with ID " + messageId + " not found"); // 채널을 찾지 못했을 경우 예외처리
  }
}
