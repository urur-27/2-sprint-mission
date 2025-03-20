package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FIleBinaryContentRepository implements BinaryContentRepository {
    @Override
    public UUID upsert(BinaryContent binaryContent) {
        return null;
    }

    @Override
    public List<BinaryContent> findAll() {
        return List.of();
    }

    @Override
    public BinaryContent findById(UUID userId) {
        return null;
    }

    @Override
    public List<BinaryContent> findAllByMessageId(UUID messageId) {
        return List.of();
    }

    @Override
    public Optional<BinaryContent> findAllByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public void deleteProfileImageByUserId(UUID userId) {

    }

    @Override
    public void deleteByMessageId(UUID messageId) {

    }
}
