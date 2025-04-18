package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.CodeitConstants;
import com.sprint.mission.discodeit.dto2.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public UserStatus create(UserStatusCreateRequest request) {
    // 관련된 User가 존재하는지
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId()));

    // 중복 여부 확인
    if (userStatusRepository.isUserOnline(request.userId(), Instant.now()
        .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS))) {
      throw new IllegalStateException("The UserStatus for that user already exists.");
    }

    UserStatus userStatus = new UserStatus(user, request.lastAccessedAt());
    return userStatusRepository.save(userStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public UserStatus findById(UUID id) {
    return userStatusRepository.findByUserId(id)
        .orElseThrow(() -> new UserStatusNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserStatus> findAll() {
    return userStatusRepository.findAllOnlineUsers(Instant.now()
        .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));
  }

  @Override
  @Transactional
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId)
        .orElseThrow(() -> new UserStatusNotFoundException(userStatusId));

    userStatus.updateLastAccessedAt(newLastActiveAt);
    return userStatus;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    UserStatus status = userStatusRepository.findById(id)
        .orElseThrow(() -> new UserStatusNotFoundException(id));

    userStatusRepository.delete(status);
  }
}
