package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto2.data.ChannelDto;
import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.InvalidChannelTypeException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.upsert(privateChannel);

    // 참여하는 User별 ReadStatus 생성
    request.userIds().forEach(userId -> {
      ReadStatus readStatus = new ReadStatus(userId, privateChannel.getId(), null);
      readStatusRepository.upsert(readStatus);
    });

    return privateChannel;
  }

  @Override
  public Channel createPublicChannel(PublicChannelCreateRequest request) {
    Channel publicChannel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.upsert(publicChannel);
    return publicChannel;
  }

  @Override
  public ChannelResponse findById(UUID id) {
    Channel channel = channelRepository.findById(id);
    if (channel == null) {
      throw new NoSuchElementException("No channel found for ID: " + id);
    }
    // 해당 채널의 가장 최근 메시지 시간 조회
    Instant lastMessageTime = messageRepository.findAll().stream()
        .filter(msg -> msg.getChannelId().equals(id))
        .findFirst()
        .map(Message::getCreatedAt)
        .orElse(null);

    // PRIVATE 채널인 경우 참여한 User ID 조회
    List<UUID> userIds = channel.getType() == ChannelType.PRIVATE
        ? readStatusRepository.findUsersByChannelId(id)
        : List.of();

    return new ChannelResponse(
        channel.getId(),
        channel.getType(),
        channel.getName(),
        channel.getDescription(),
        lastMessageTime,
        userIds
    );
  }

  @Override
  public List<ChannelDto> findAllByUserId(UUID userId) {
    List<Channel> allChannels = channelRepository.findAll();
    List<ChannelDto> responses = new ArrayList<>();

    for (Channel channel : allChannels) {
      Instant lastMessageTime = messageRepository.findAll().stream()
          .filter(msg -> msg.getChannelId().equals(channel.getId()))
          .map(Message::getCreatedAt)
          .max(Instant::compareTo)
          .orElse(null);

      List<UUID> userIds = channel.getType() == ChannelType.PRIVATE
          ? readStatusRepository.findUsersByChannelId(channel.getId())
          : List.of();

      // PRIVATE 채널은 참여한 User만 조회 가능
      if (channel.getType() == ChannelType.PRIVATE && !userIds.contains(userId)) {
        continue;
      }

      responses.add(new ChannelDto(
          channel.getId(),
          channel.getType(),
          channel.getName(),
          channel.getDescription(),
          userIds,
          lastMessageTime
      ));
    }
    return responses;
  }

  @Override
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId);
    if (channel == null) {
      throw new ChannelNotFoundException(channelId);
    }

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new InvalidChannelTypeException(
          "Private channels cannot be modified via this endpoint.");
    }

    channel.updateChannel(ChannelType.PUBLIC, request.newName(), request.newDescription());
    channelRepository.upsert(channel);
    return channel;
  }

  @Override
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id);

    if (channel == null) {
      throw new ChannelNotFoundException(id);
    }

    // 관련 도메인 같이 삭제
    messageRepository.delete(id);
    readStatusRepository.deleteByChannelId(id);
    channelRepository.delete(id);
  }
}