package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;

  @Override
  @Transactional
  public Channel createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.save(privateChannel);

    List<User> users = request.userIds().stream()
        .map(userId -> userRepository.findById(userId)
            .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND)))
        .toList();

    users.forEach(user -> {
      ReadStatus readStatus = new ReadStatus(user, privateChannel, privateChannel.getCreatedAt());
      readStatusRepository.save(readStatus);
    });

    return privateChannel;
  }

  @Override
  @Transactional
  public Channel createPublicChannel(PublicChannelCreateRequest request) {
    if (request.name().isBlank()) {
      throw new RestException(ResultCode.INVALID_CHANNEL_DATA);
    }

    Channel publicChannel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.save(publicChannel);
    return publicChannel;
  }

  @Override
  @Transactional(readOnly = true)
  public Channel findById(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new RestException(ResultCode.CHANNEL_NOT_FOUND));
    return channel;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Channel> findAllByUserId(UUID userId) {
    return channelRepository.findAll().stream()
        .filter(channel -> {
          if (channel.getType() == ChannelType.PRIVATE) {
            List<UUID> userIds = readStatusRepository.findUsersByChannelId(channel.getId());
            return userIds.contains(userId); // PRIVATE이면 참가 여부 확인
          }
          return true; // PUBLIC이면 무조건 포함
        })
        .toList();
  }

  @Override
  @Transactional
  public Channel update(UUID channelId, PublicChannelUpdateRequest request) {
    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> new RestException(ResultCode.CHANNEL_NOT_FOUND));

    if (channel.getType() == ChannelType.PRIVATE) {
      throw new RestException(ResultCode.INVALID_CHANNEL_TYPE);
    }

    if (request.newName().isBlank()) {
      throw new RestException(ResultCode.INVALID_CHANNEL_DATA);
    }

    // 변경 감지 방식 적용(Dirty Checking)
    channel.updateChannel(ChannelType.PUBLIC, request.newName(), request.newDescription());
    return channel;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Channel channel = channelRepository.findById(id)
        .orElseThrow(() -> new RestException(ResultCode.CHANNEL_NOT_FOUND));

    // 관련 도메인 같이 삭제
    List<Message> messages = messageRepository.findByChannelId(id);

    // cascade 없음을 가정 message, readstatus는 수동 삭제
    messageRepository.deleteAll(messages);
    readStatusRepository.deleteByChannelId(id);
    channelRepository.delete(channel);
  }
}