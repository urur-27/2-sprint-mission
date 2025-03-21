package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> data = new HashMap<>();

    @Override
    public UUID upsert(BinaryContent binaryContent) {
        data.put(binaryContent.getId(), binaryContent);
        return binaryContent.getId();
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public BinaryContent findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(UUID messageId) {
        return data.values().stream()
                .filter(content -> content.getId().equals(messageId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        data.entrySet().removeIf(entry -> entry.getValue().getId().equals(messageId));
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

