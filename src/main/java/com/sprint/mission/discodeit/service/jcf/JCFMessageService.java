package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

//Message 생성 시 연관된 도메인 모델 데이터 확인하기. Message는 Channel과 User에 의존하고 있음
public class JCFMessageService implements MessageService {
    private final UserService userService;
    private final ChannelService channelService;

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, Message> data;


    public JCFMessageService(UserService userService, ChannelService channelService)// private 생성자로 외부에서 인스턴스 생성 방지
    {
        this.userService = userService;
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    // 메시지 생성(내용, 보낸 사람, 보낸 채널)
    @Override
    public UUID createMessage(String content, UUID senderId, UUID channelId) {
        User sender = findUserById(senderId);
        Channel channel = findChannelById(channelId);

        Message message = new Message(content, sender, channel);
        data.put(message.getId(), message);
        return message.getId();
    }

    // UUID 기반 메시지 조회
    @Override
    public Message getMessageById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("No data for that ID could be found.: " + id));
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
        if (message == null) {
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
        message.updateMessage(content);
    }

    // UUID 기반으로 삭제
    @Override
    public void deleteMessage(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
        data.remove(id);
    }

    // User ID 검증
    private User findUserById(UUID id) {
        return Optional.ofNullable(userService.getUserById(id))
                .orElseThrow(() -> new NoSuchElementException("User does not exist: " + id));
    }

    // Channel ID 검증
    private Channel findChannelById(UUID id) {
        return Optional.ofNullable(channelService.getChannelById(id))
                .orElseThrow(() -> new NoSuchElementException("Channel does not exist: " + id));
    }
}