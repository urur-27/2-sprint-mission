package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.CodeitConstants;
import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserStatus create(UserStatusCreateRequest request) {
    String traceId = MDC.get("traceId");
    UUID userId = request.userId();

    // 시작 로그
    log.info("[CREATE] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    // 관련된 User가 존재하는지
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> {
          log.warn("[CREATE] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    // 중복 여부 확인
    if (userStatusRepository.isUserOnline(request.userId(), Instant.now()
        .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS))) {
      log.warn("[CREATE] User status already online: userId={}, traceId={}",
          LogUtils.maskUUID(userId), traceId);
      throw new RestException(ResultCode.USER_STATUS_NOT_FOUND);
    }

    UserStatus userStatus = UserStatus.builder()
        .user(user)
        .lastActiveAt(request.lastAccessedAt())
        .build();

    userStatusRepository.save(userStatus);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, userStatusId={}, traceId={}",
        userStatus.getId(), traceId);
    return userStatus;
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatus findById(UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    UserStatus userStatus = userStatusRepository.findByUserId(userId)
        .orElseThrow(() -> {
          log.warn("[FIND] UserStatus not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_STATUS_NOT_FOUND);
        });

    // 성공 로그
    log.info("[FIND] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(userId), traceId);
    return userStatus;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatus> findAll() {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, traceId={}", traceId);

    List<UserStatus> userStatuses = userStatusRepository.findAllOnlineUsers(
        Instant.now().minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userStatusCount={}, traceId={}",
        userStatuses.size(), traceId);
    return userStatuses;
  }

  @Override
  @Transactional
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, userStatusId={}, traceId={}",
        log.isDebugEnabled() ? userStatusId : LogUtils.maskUUID(userStatusId), traceId);

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("[UPDATE] UserStatus not found: userStatusId={}, traceId={}",
              LogUtils.maskUUID(userStatusId), traceId);
          return new RestException(ResultCode.USER_STATUS_NOT_FOUND);
        });

    userStatus.updateLastAccessedAt(newLastActiveAt);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, userStatusId={}, traceId={}",
        LogUtils.maskUUID(userStatusId), traceId);
    return userStatus;
  }

  @Override
  @Transactional
  public void delete(UUID userStatusId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, userStatusId={}, traceId={}",
        log.isDebugEnabled() ? userStatusId : LogUtils.maskUUID(userStatusId), traceId);

    UserStatus status = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> {
          log.warn("[DELETE] UserStatus not found: userStatusId={}, traceId={}",
              LogUtils.maskUUID(userStatusId), traceId);
          return new RestException(ResultCode.USER_STATUS_NOT_FOUND);
        });

    userStatusRepository.delete(status);
    
    // 성공 로그
    log.info("[DELETE] status=SUCCESS, userStatusId={}, traceId={}",
        LogUtils.maskUUID(userStatusId), traceId);
  }
}
