package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void upsert(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String newContent) {
        Message message = data.get(id);
        if (message != null) {
            message.updateMessage(newContent);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}