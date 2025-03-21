package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto2.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

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

    static void createBinaryContent(BinaryContentService binaryContentService) {
        BinaryContentCreateRequest binaryRequest = new BinaryContentCreateRequest(
                new byte[]{1, 2, 3, 4, 5},
                "testType/test",
                5
        );
        UUID binaryContentId = binaryContentService.create(binaryRequest);
        System.out.println("BinaryContent 생성: " + binaryContentId);
    }

    static void createReadStatus(ReadStatusService readStatusService, UUID userId, UUID channelId) {
        // ReadStatus 생성
        ReadStatusCreateRequest readStatusRequest = new ReadStatusCreateRequest(
                userId, channelId, Instant.now()
        );
        UUID readStatusId = readStatusService.create(readStatusRequest);
        System.out.println("ReadStatus created: " + readStatusId);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 빈 가져오기
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);

        // 셋업
        UUID user = setupUser(userService);
        UUID channel = setupChannel(channelService);

        // 테스트
        messageCreateTest(messageService, user, channel);
        createBinaryContent(binaryContentService);
        createReadStatus(readStatusService, user, channel);
        readStatusService.findAllByUserId(user).forEach(readStatus ->
                System.out.printf("User ID: %s, Channel ID: %s, Last Read At: %s%n",
                        readStatus.getUserId(),
                        readStatus.getChannelId(),
                        readStatus.getLastReadAt())
        );
        System.out.println("User id: \""+userStatusService.findById(user).getUserId() + "\" is online " + userStatusService.findById(user).isCurrentOnline()); ;
    }
}
