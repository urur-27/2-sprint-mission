package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;

public class JavaApplication {
    static UUID setupUser(UserService userService) {
        UUID userId = userService.create("woody", "woody@codeit.com");
        System.out.println("유저 생성: " + userId);
        return userId;
    }

    static UUID setupChannel(ChannelService channelService) {
        UUID channelId = channelService.create("공지");
        System.out.println("채널 생성: " + channelId);
        return channelId;
    }

    static void messageCreateTest(MessageService messageService, UUID senderId, UUID channelId) {
        UUID message = messageService.create("안녕하세요.", senderId, channelId);
        System.out.println("메시지 생성: " + message);
    }

    public static void main(String[] args) {
        // 서비스 초기화
        // TODO Basic*Service 구현체를 초기화하세요.
        UserService userService = ServiceFactory.getInstance().getUserService();
        ChannelService channelService = ServiceFactory.getInstance().getChannelService();
        MessageService messageService = ServiceFactory.getInstance().getMessageService();

        // 셋업
        UUID user = setupUser(userService);
        UUID channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, user, channel);
    }
}