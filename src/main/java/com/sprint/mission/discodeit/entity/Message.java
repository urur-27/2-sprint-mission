package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Message extends BaseEntity {
    // 채널에 메시지를 보낼 수 있는 기능
    // 해당 채널에 들어가면 "누구누구 : 메시지" 이런 식으로 대화가 표현되게
    // 보낸 User의 정보와 Channel 정보 포함
    private String content;
    private final User sender;
    private final Channel channel;

    public Message(String content, User sender, Channel channel) {
        super();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
    }

    // 메시지 수정 메서드
    public void updateMessage(String content) {
        this.content = content;
        updateTimestamp();
    }
}