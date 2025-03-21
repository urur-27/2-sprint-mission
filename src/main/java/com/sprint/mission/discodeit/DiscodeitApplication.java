package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto2.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.UserCreateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    static UUID setupUser(UserService userService) {
        // UserCreateRequest DTO 생성
        UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody@codeit.com", null);

        // 변경된 create 메서드 호출
        UUID userId = userService.create(request);

        System.out.println("유저 생성: " + userId);
        return userId;
    }

    static UUID setupChannel(ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지사항을 안내하는 채널입니다.");
        UUID channelId = channelService.createPublicChannel(request);
        System.out.println("채널 생성: " + channelId);
        return channelId;
    }

    static void messageCreateTest(MessageService messageService, UUID senderId, UUID channelId) {
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요",senderId, channelId, null);
        UUID message = messageService.create(request);
        System.out.println("메시지 생성: " + message);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 빈 가져오기
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        UUID user = setupUser(userService);
        UUID channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, user, channel);
    }
}
