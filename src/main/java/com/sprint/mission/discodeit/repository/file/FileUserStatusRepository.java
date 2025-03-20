package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {
    @Override
    public void upsert(UserStatus userStatus) {

    }

    @Override
    public boolean isUserOnline(UUID userId) {
        return false;
    }

    @Override
    public List<UserStatus> findAllOnlineUsers() {
        return List.of();
    }

    @Override
    public void updateLastAccessedAt(UUID userId, Instant lastAccessedAt) {

    }

    @Override
    public void deleteByUserId(UUID userId) {

    }
}
