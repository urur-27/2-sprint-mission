package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.invalid.InvalidChannelTypeException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;
  private final ReadStatusRepository readStatusRepository;
  private final MessageRepository messageRepository;
  private final ChannelMapper channelMapper;

  @Override
  public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
    Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
    channelRepository.upsert(privateChannel);

    List<User> users = request.userIds().stream()
        .map(userId -> {
          User user = userRepository.findById(userId);
          if (user == null) {
            throw new UserNotFoundException(userId);
          }
          return user;
        })
        .toList();

    users.forEach(user -> {
      ReadStatus readStatus = new ReadStatus(user, privateChannel, privateChannel.getCreatedAt());
      readStatusRepository.upsert(readStatus);
    });

    return channelMapper.toResponse(privateChannel);
  }

  @Override
  public ChannelResponse createPublicChannel(PublicChannelCreateRequest request) {
    Channel publicChannel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
    channelRepository.upsert(publicChannel);
    return channelMapper.toResponse(publicChannel);
  }

  @Override
  public ChannelResponse findById(UUID id) {
    Channel channel = channelRepository.findById(id);
    if (channel == null) {
      throw new ChannelNotFoundException(id);
    }

    return channelMapper.toResponse(channel);

  }

  @Override
  public List<ChannelResponse> findAllByUserId(UUID userId) {

    return channelRepository.findAll().stream()
        .filter(channel -> {
          if (channel.getType() == ChannelType.PRIVATE) {
            List<UUID> userIds = readStatusRepository.findUsersByChannelId(channel.getId());
            return userIds.contains(userId); // PRIVATE이면 참가 여부 확인
          }
          return true; // PUBLIC이면 무조건 포함
        })
        .map(channelMapper::toResponse)
        .toList();
  }

  @Override
  public ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request) {
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
    return channelMapper.toResponse(channel);
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