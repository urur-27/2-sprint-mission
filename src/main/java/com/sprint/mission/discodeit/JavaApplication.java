package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 등록, 조회(단건, 다건), 수정, 수정된 데이터 조회, 삭제, 조회를 통해 삭제되었는지 확인
        UserService userService = ServiceFactory.getUserService();
        ChannelService channelService = ServiceFactory.getChannelService();
        MessageService messageService = ServiceFactory.getMessageService();

        // 등록(User). Kim - Kim@google.com, min - min@naver.com
        System.out.println("=== [User create] ===");
        UUID kimId = userService.createUser("Kim", "Kim@google.com");
        UUID minId = userService.createUser("min", "min@naver.com");
        printAllUsers(userService);

        // 등록(Channel). First, Second
        System.out.println("\n=== [Channel create] ===");
        UUID firstChannelId = channelService.createChannel("First");
        UUID secondChannelId = channelService.createChannel("Second");
        printAllChannels(channelService);

        // 단건 조회(User). Kim 조회
        System.out.println("\n=== [User search] ===");
        User kim = userService.getUserById(kimId);
        printUser(kim);

        // 단건 조회(Channel). First 조회
        System.out.println("\n=== [Channel search] ===");
        Channel firstChannel = channelService.getChannelById(firstChannelId);
        printChannel(firstChannel);

        // 등록(Message). Kim이 First채널에서 보내는 Message
        System.out.println("\n=== [Message create] ===");
        UUID kimMessageId = messageService.createMessage("First channel message - by Kim", kimId, firstChannelId);
        printAllMessages(messageService);

        System.out.println("\n=== [Message create] ===");
        UUID minMessageId = messageService.createMessage("Second channel message - by min", minId, secondChannelId);
        printAllMessages(messageService);

        // 단건 조회(Message). Kim이 작성한 메시지 조회
        System.out.println("\n=== [Message search] ===");
        Message kimMessage = messageService.getMessageById(kimMessageId);
        printMessage(kimMessage);

        // 다건 조희(User)
        System.out.println("\n=== [Searh all users] ===");
        printAllUsers(userService);

        // 다건 조회(Channel)
        System.out.println("\n=== [Search all channels] ===");
        printAllChannels(channelService);

        // 다건 조회(Message)
        System.out.println("\n=== [Search all Messages] ===");
        printAllMessages(messageService);

        // User 수정: "Kim"를 "KimUpdated"로 변경
        System.out.println("\n=== [User Update] ===");
        System.out.print("[Before]: ");
        printUser(kim);
        userService.updateUser(kimId, "KimUpdated", "upKim@google.com");
        System.out.print("[Updated]: ");
        printUser(kim);

        // Message 수정. Kim이 작성한 메시지 수정
        System.out.println("\n=== [Message Update] ===");
        System.out.print("[Before]: ");
        printMessage(kimMessage);
        messageService.updateMessage(kimMessageId, "Updated message by Kim");
        System.out.print("[Updated]: ");
        printMessage(kimMessage);

        // Channel 수정. First 채널 이름 수정
        System.out.println("\n=== [Channel Update] ===");
        System.out.print("[Before]: ");
        printMessage(kimMessage);
        channelService.updateChannel(firstChannelId, "Updated First Channel");
        System.out.print("[Updated]: ");
        printMessage(kimMessage);

        // User 삭제. Kim 삭제
        System.out.println("\n=== [User Delete] ===");
        userService.deleteUser(kim.getId());
        System.out.println("Delete user Kim");
        printAllUsers(userService);

        // Channel 삭제: "First" 채널 삭제
        System.out.println("\n=== [Channel delete] ===");
        channelService.deleteChannel(firstChannelId);
        System.out.println("Channel 'First' deleted.");
        printAllChannels(channelService);

        // Message 삭제. Kim이 작성한 메시지 삭제
        System.out.println("\n=== [Message Delete] ===");
        messageService.deleteMessage(kimMessageId);
        System.out.println("Delete message by Kim");
        printAllMessages(messageService);
    }


    private static void printAllUsers(UserService userService) {
        System.out.println("=== [All Users] ===");
        userService.getAllUsers().forEach(JavaApplication::printUser);
    }

    private static void printAllChannels(ChannelService channelService) {
        System.out.println("=== [All Channels] ===");
        channelService.getAllChannels().forEach(JavaApplication::printChannel);
    }

    private static void printAllMessages(MessageService messageService) {
        System.out.println("=== [All Messages] ===");
        messageService.getAllMessages().forEach(JavaApplication::printMessage);
    }

    private static void printUser(User user) {
        System.out.println("User: " + user.getUsername()
                + " | Email: " + user.getEmail()
                + " | Created At: " + formatTime(user.getCreatedAt()));
    }

    private static void printChannel(Channel channel) {
        System.out.println("Channel: " + channel.getName()
                + " | Created At: " + formatTime(channel.getCreatedAt()));
    }

    private static void printMessage(Message message) {
        System.out.println("Message: " + message.getContent()
                + " | Sender: " + message.getSender().getUsername()
                + " | Channel: " + message.getChannel().getName()
                + " | Created At: " + formatTime(message.getCreatedAt()));
    }

    private static String formatTime(long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("UTC")).toString();
    }
}