package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public UUID create(String content, UUID senderId, UUID channelId) {

        Message message = new Message(content, senderId, channelId);
        messageRepository.upsert(message);
        return message.getId();
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(messageRepository.findById(id))
                .orElseThrow(() -> new NoSuchElementException("No message found for ID: " + id));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public void update(UUID id, String messageName) {
        // 검증
        Message message = findById(id);
        messageRepository.update(id, messageName);
    }

    @Override
    public void delete(UUID id) {
        // 검증
        Message message = findById(id);
        messageRepository.delete(id);
    }

}
