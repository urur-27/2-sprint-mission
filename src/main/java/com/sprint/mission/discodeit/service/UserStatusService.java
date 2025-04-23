package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  UserStatus create(UserStatusCreateRequest request);

  UserStatus findById(UUID id);

  List<UserStatus> findAll();

  UserStatus update(UUID userStatusId, UserStatusUpdateRequest request);

  void delete(UUID id);
}
