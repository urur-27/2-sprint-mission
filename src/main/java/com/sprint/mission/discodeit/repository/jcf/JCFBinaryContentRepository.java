package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFBinaryContentRepository implements BinaryContentRepository {
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
