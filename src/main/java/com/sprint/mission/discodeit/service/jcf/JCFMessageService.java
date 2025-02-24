package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
//Message 생성 시 연관된 도메인 모델 데이터 확인하기. Message는 Channel과 User에 의존하고 있음
public class JCFMessageService implements MessageService {
    private final JCFUserService userService;
    private final JCFChannelService channelService;

    private static JCFMessageService instance; // 정적 변수로 유일한 인스턴스 저장

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, Message> data;


    private JCFMessageService(JCFUserService userService, JCFChannelService channelService)// private 생성자로 외부에서 인스턴스 생성 방지
    {
        this.userService = userService;
        this.channelService = channelService;
        this.data = new HashMap<>();
    }

    public static JCFMessageService getInstance(JCFUserService userService, JCFChannelService channelService) {
        if (instance == null) {
            instance = new JCFMessageService(userService, channelService);
        }
        return instance;
    }

    // 메시지 생성(내용, 보낸 사람, 보낸 채널)
    @Override
    public void createMessage(String content, UUID senderId, UUID channelId) {
        User sender = userService.getUserById(senderId);
        Channel channel = channelService.getChannelById(channelId);

        if (sender == null) {
            throw new IllegalArgumentException("User does not exist.");
        }
        if (channel == null) {
            throw new IllegalArgumentException("Channel does not exist.");
        } // sender와 channel 검증 단계

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
