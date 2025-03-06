package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    //CRUD 기능을 선언
    UUID create(String content, UUID senderId, UUID channelId);
    Message findById(UUID id);
    List<Message> findAll();
    void update(UUID id, String content);
    void delete(UUID id);
}