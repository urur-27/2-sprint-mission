package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    //CRUD 기능을 선언
    UUID create(MessageCreateRequest request);
    Message findById(UUID id);
    List<Message> findAllByChannelId(UUID channelId);
    void update(MessageUpdateRequest request);
    void delete(UUID id);
}