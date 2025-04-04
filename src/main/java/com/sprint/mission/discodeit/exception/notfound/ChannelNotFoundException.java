package com.sprint.mission.discodeit.exception.notfound;

import java.util.UUID;

public class ChannelNotFoundException extends RuntimeException {

  public ChannelNotFoundException(UUID channelId) {
    super("Channel with ID " + channelId + " not found"); // 채널을 찾지 못했을 경우 예외처리
  }
}
