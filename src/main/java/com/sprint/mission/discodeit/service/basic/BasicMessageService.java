package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private static volatile BasicMessageService instance;
    private final MessageRepository messageRepository;

    private final UserService userService;
    private final ChannelService channelService;

    private BasicMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        this.userService = userService;
        this.channelService = channelService;
        this.messageRepository = messageRepository;  // 저장소 주입
    }

    // 기본 저장소를 FileMessageRepository로 설정
    public static BasicMessageService getInstance(UserService userService, ChannelService channelService) {
        return getInstance(userService,channelService,new FileMessageRepository());
    }

    public static BasicMessageService getInstance(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
        if (instance == null) {
            synchronized (BasicMessageService.class) {
                if (instance == null) {
                    instance = new BasicMessageService(userService, channelService, messageRepository);
                }
            }
        }
        return instance;
    }

    @Override
    public UUID create(String content, UUID senderId, UUID channelId) {
        User sender = findUserById(senderId);
        Channel channel = findChannelById(channelId);

        Message message = new Message(content, sender, channel);
        messageRepository.create(message);
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

    // User ID 검증
    private User findUserById(UUID id) {
        return Optional.ofNullable(userService.findById(id))
                .orElseThrow(() -> new NoSuchElementException("User does not exist: " + id));
    }

    // Channel ID 검증
    private Channel findChannelById(UUID id) {
        return Optional.ofNullable(channelService.findById(id))
                .orElseThrow(() -> new NoSuchElementException("Channel does not exist: " + id));
    }
}
