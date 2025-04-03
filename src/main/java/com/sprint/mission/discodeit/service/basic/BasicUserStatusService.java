package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

  private final UserStatusRepository userStatusRepository;
  private final UserRepository userRepository;

  @Override
  public UserStatus create(UserStatusCreateRequest request) {
    // 관련된 User가 존재하는지
    User user = userRepository.findById(request.userId());
    if (user == null) {
      throw new IllegalArgumentException("That user does not exist.");
    }

    // 중복 여부 확인
    if (userStatusRepository.isUserOnline(request.userId())) {
      throw new IllegalStateException("The UserStatus for that user already exists.");
    }

    UserStatus userStatus = new UserStatus(request.userId(), request.lastAccessedAt());
    userStatusRepository.upsert(userStatus);

    return userStatus;
  }

  @Override
  public UserStatus findById(UUID id) {
    return userStatusRepository.findByUserId(id);
  }

  @Override
  public List<UserStatus> findAll() {
    return userStatusRepository.findAllOnlineUsers();
  }

  @Override
  public UserStatus update(UUID userStatusId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findById(userStatusId);
    if (userStatus == null) {
      throw new UserStatusNotFoundException(userStatusId);
    }
    userStatus.updateLastAccessedAt(newLastActiveAt);

    return userStatusRepository.upsert(userStatus);
  }

  @Override
  public UserStatus updateByUserId(UUID userId, UserStatusUpdateRequest request) {
    Instant newLastActiveAt = request.newLastActiveAt();

    UserStatus userStatus = userStatusRepository.findByUserId(userId);
    if (userStatus == null) {
      throw new UserStatusNotFoundException(userId);
    }

    userStatus.updateLastAccessedAt(newLastActiveAt);
    return userStatusRepository.upsert(userStatus);
  }

  @Override
  public void delete(UUID id) {
    UserStatus existing = findById(id);

    if (existing == null) {
      throw new IllegalArgumentException("The UserStatus does not exist.");
    }

    userStatusRepository.deleteByUserId(existing.getUserId());
  }
}
