package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UUID create(UserStatusCreateRequest request);
    UserStatus findById(UUID id);
    List<UserStatus> findAll();
    void update(UserStatusUpdateRequest request);
    void updateByUserId(UUID userId, Instant lastAccessedAt);
    void delete(UUID id);
}
