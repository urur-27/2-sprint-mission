package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
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
    public UUID create(UserStatusCreateRequest request) {
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

        return userStatus.getId();
    }

    @Override
    public UserStatus findById(UUID id) {
        return null;
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAllOnlineUsers();
    }

    @Override
    public void update(UserStatusUpdateRequest request) {
        UserStatus existing = findById(request.id());

        if (existing == null) {
            throw new IllegalArgumentException("The UserStatus does not exist.");
        }

        existing.updateLastAccessedAt(request.lastAccessedAt());
        userStatusRepository.upsert(existing);
    }

    @Override
    public void updateByUserId(UUID userId, Instant lastAccessedAt) {
        if (!userStatusRepository.isUserOnline(userId)) {
            throw new IllegalArgumentException("The UserStatus for that user does not exist.");
        }
        userStatusRepository.updateLastAccessedAt(userId, lastAccessedAt);
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
