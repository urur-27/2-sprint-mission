package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelMapper {

  private final MessageRepository messageRepository;
  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final UserMapper userMapper;

  public ChannelResponse toResponse(Channel channel) {
    // 가장 최근 메시지 시간 계산
    Instant lastMessageAt = messageRepository.findAll().stream()
        .filter(msg -> msg.getChannel().getId().equals(channel.getId()))
        .map(Message::getCreatedAt)
        .max(Comparator.naturalOrder())
        .orElse(null);

    // 참가자 목록 가져오기 (PRIVATE일 경우만)
    List<UserResponse> participants = channel.getType() == ChannelType.PRIVATE
        ? readStatusRepository.findUsersByChannelId(channel.getId()).stream()
        .map(userRepository::findById)
        .filter(Objects::nonNull)
        .map(userMapper::toResponse)
        .toList()
        : List.of();

    // ChannelResponse 구성
    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        lastMessageAt,
        participants
    );
  }
}
