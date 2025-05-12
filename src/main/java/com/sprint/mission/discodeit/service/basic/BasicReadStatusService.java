package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.util.LogUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

  private final ReadStatusRepository readStatusRepository;
  private final UserRepository userRepository;
  private final ChannelRepository channelRepository;


  @Override
  @Transactional
  public ReadStatus create(ReadStatusCreateRequest request) {
    String traceId = MDC.get("traceId");
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    // 시작 로그
    log.info("[CREATE] status=START, userId={}, channelId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId),
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId),
        traceId);

    // 관련된 User와 Channel이 존재하는지 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[CREATE] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("[CREATE] Channel not found: channelId={}, traceId={}",
              LogUtils.maskUUID(channelId), traceId);
          return new RestException(ResultCode.CHANNEL_NOT_FOUND);
        });

    ReadStatus newReadStatus = ReadStatus.builder()
        .user(user)
        .channel(channel)
        .lastReadAt(request.lastReadAt())
        .build();

    readStatusRepository.save(newReadStatus);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, readStatusId={}, traceId={}",
        newReadStatus.getId(), traceId);
    return newReadStatus;
  }

  // ID로 ReadStatus 조회
  @Override
  @Transactional(readOnly = true)
  public ReadStatus findById(UUID readStatusId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, readStatusId={}, traceId={}",
        log.isDebugEnabled() ? readStatusId : LogUtils.maskUUID(readStatusId), traceId);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("[FIND] ReadStatus not found: readStatusId={}, traceId={}",
              LogUtils.maskUUID(readStatusId), traceId);
          return new RestException(ResultCode.READ_STATUS_NOT_FOUND);
        });

    // 성공 로그
    log.info("[FIND] status=SUCCESS, readStatusId={}, traceId={}",
        LogUtils.maskUUID(readStatusId), traceId);
    return readStatus;
  }

  // 사용자의 Id로 조회
  @Override
  @Transactional(readOnly = true)
  public List<ReadStatus> findAllByUserId(UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    List<ReadStatus> readStatuses = readStatusRepository.findAllByUserId(userId);

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userId={}, readStatusCount={}, traceId={}",
        LogUtils.maskUUID(userId), readStatuses.size(), traceId);
    return readStatuses;
  }

  // ReadStatus 업데이트
  @Override
  @Transactional
  public ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, readStatusId={}, traceId={}",
        log.isDebugEnabled() ? readStatusId : LogUtils.maskUUID(readStatusId), traceId);

    Instant newLastReadAt = request.newLastReadAt();
    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("[UPDATE] ReadStatus not found: readStatusId={}, traceId={}",
              LogUtils.maskUUID(readStatusId), traceId);
          return new RestException(ResultCode.READ_STATUS_NOT_FOUND);
        });

    readStatus.updateReadStatus(newLastReadAt);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, readStatusId={}, traceId={}",
        LogUtils.maskUUID(readStatusId), traceId);
    return readStatus;
  }

  // ReadStatus 삭제
  @Override
  @Transactional
  public void delete(UUID readStatusId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, readStatusId={}, traceId={}",
        log.isDebugEnabled() ? readStatusId : LogUtils.maskUUID(readStatusId), traceId);

    ReadStatus readStatus = readStatusRepository.findById(readStatusId)
        .orElseThrow(() -> {
          log.warn("[DELETE] ReadStatus not found: readStatusId={}, traceId={}",
              LogUtils.maskUUID(readStatusId), traceId);
          return new RestException(ResultCode.READ_STATUS_NOT_FOUND);
        });

    readStatusRepository.delete(readStatus);

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, readStatusId={}, traceId={}",
        LogUtils.maskUUID(readStatusId), traceId);
  }
}