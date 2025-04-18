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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  @Transactional
  public ReadStatus create(ReadStatusCreateRequest request) {
    // 관련된 User와 Channel이 존재하는지 확인
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

    ReadStatus newReadStatus = new ReadStatus(user, channel,
        request.lastReadAt());
    return readStatusRepository.save(newReadStatus);
  }

  // ID로 ReadStatus 조회
  @Override
  @Transactional(readOnly = true)
  public ReadStatus findById(UUID readStatusId) {
    return readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
  }

  // 사용자의 Id로 조회
  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return readStatusRepository.findAllByUserId(userId);
  }

  // ReadStatus 업데이트
  @Override
  @Transactional
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

    readStatus.updateReadStatus(newLastReadAt);
    return readStatus;
  }

  // ReadStatus 삭제
  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));

    readStatusRepository.delete(readStatus);
  }

}