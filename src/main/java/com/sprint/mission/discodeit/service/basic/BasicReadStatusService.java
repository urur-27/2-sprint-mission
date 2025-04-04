package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.notfound.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  public ReadStatus create(ReadStatusCreateRequest request) {
    // 관련된 User와 Channel이 존재하는지 확인
    User user = userRepository.findById(request.userId());
    if (user == null) {
      throw new UserNotFoundException(request.userId());
    }

    Channel channel = channelRepository.findById(request.channelId());
    if (channel == null) {
      throw new ChannelNotFoundException(request.channelId());
    }

    // 중복 방지
    readStatusRepository.findAllByUser(request.userId()).forEach(r -> {
      if (r.getChannelId().equals(request.channelId())) {
        throw new DuplicateReadStatusException(request.userId(), request.channelId());
      }
    });

    ReadStatus newReadStatus = new ReadStatus(request.userId(), request.channelId(),
        request.lastReadAt());
    readStatusRepository.upsert(newReadStatus);
    return newReadStatus;
  }

  // ID로 ReadStatus 조회
  @Override
  public ReadStatus find(UUID readStatusId) {
    return Optional.ofNullable(readStatusRepository.findById(readStatusId))
        .orElseThrow(
            () -> new NoSuchElementException("read status not found with id: " + readStatusId));
  }

  // 사용자의 Id로 조회
  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUser(userId);
  }

  // ReadStatus 업데이트
  @Override
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId);
    if (readStatus == null) {
      throw new ReadStatusNotFoundException(readStatusId);
    }

    readStatus.updateReadStatus(newLastReadAt);
    return readStatusRepository.upsert(readStatus);
  }

  // ReadStatus 삭제
  @Override
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId);
    if (readStatus == null) {
      throw new NoSuchElementException("read status not found with id: " + readStatusId);
    }

    readStatusRepository.delete(readStatusId);
  }
}