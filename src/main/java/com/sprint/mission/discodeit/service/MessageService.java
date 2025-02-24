package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    //CRUD 기능을 선언
    void createMessage(String content, UUID senderId, UUID channelId);
    Message getMessageById(UUID id);
    List<Message> getAllMessages();
    void updateMessage(UUID id, String content);
    void deleteMessage(UUID id);
}