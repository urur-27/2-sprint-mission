package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    @Override
    public void upsert(ReadStatus readStatus) {

    }

    @Override
    public List<UUID> findUsersByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        return null;
    }

    @Override
    public List<ReadStatus> findAllByUser(UUID userId) {
        return List.of();
    }

    @Override
    public void updateLastReadAt(UUID userId, UUID channelId, Instant lastReadAt) {

    }

    @Override
    public void deleteByUserId(UUID userId) {

    }

    @Override
    public void deleteByChannelId(UUID channelId) {

    }

    @Override
    public void delete(UUID readStatusId) {

    }
}
