package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    // 메시지 생성(내용, 보낸 사람, 보낸 채널)
    @Override
    public void createMessage(String content, User sender, Channel channel) {
        Message message = new Message(content, sender, channel);
        data.put(message.getId(), message);
    }

    // UUID 기반 메시지 조회
    @Override
    public Message getMessageById(UUID id) {
        return data.get(id);
    }

    // 모든 메시지 조회
    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    // UUID를 기반으로 메시지 수정
    @Override
    public void updateMessage(UUID id, String content) {
        Message message = data.get(id);
        if (message != null) {
            message.updateMessage(content);
        }    }

    // UUID 기반으로 삭제
    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
