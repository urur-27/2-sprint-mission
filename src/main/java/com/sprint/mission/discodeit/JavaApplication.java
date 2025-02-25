package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;

public class JavaApplication {
    public static void main(String[] args) {
        // 등록, 조회(단건, 다건), 수정, 수정된 데이터 조회, 삭제, 조회를 통해 삭제되었는지 확인
        UserService userService = ServiceFactory.getUserService();
        ChannelService channelService = ServiceFactory.getChannelService();
        MessageService messageService = ServiceFactory.getMessageService();

        // 등록(User). Kim - Kim@google.com, min - min@naver.com
        System.out.println("=== [User create] ===");
        userService.createUser("Kim", "Kim@google.com");
        userService.createUser("min", "min@naver.com");

        // 등록(Channel). First, Second
        System.out.println("\n=== [Channel create] ===");
        channelService.createChannel("First");
        channelService.createChannel("Second");

        // 단건 조회(User). Kim 조회
        System.out.println("\n=== [User search] ===");
        User Kim = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("Kim"))
                .findFirst()
                .orElse(null); // Kim 정보 받아오기. 없으면 null

        // 단건 조회(Channel). First 조회
        System.out.println("=== [Channel search] ===");
        Channel FirstChannel =
                channelService.getAllChannels().stream()
                        .filter(ch -> ch.getName().equalsIgnoreCase("First"))
                        .findFirst()
                        .orElse(null); // First 정보 받아오기

        // 등록(Message). Kim이 First채널에서 보내는 Message
        System.out.println("\n=== [Message create] ===");
        messageService.createMessage("First channel message - by Kim",Kim.getId(),FirstChannel.getId());


        // min이 Second채널에서 보내는 Message
        System.out.println("\n=== [Message create] ===");
        User min = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("min"))
                .findFirst()
                .orElse(null); // min 정보 받아오기. 없으면 null
        Channel secondChannel = channelService.getAllChannels().stream()
                .filter(ch -> ch.getName().equalsIgnoreCase("Second"))
                .findFirst()
                .orElse(null); // Second 정보 받아오기
        messageService.createMessage("Second channel message - by min", min.getId(), secondChannel.getId());



        // 단건 조회(Message). Kim이 작성한 메시지 조회
        System.out.println("\n=== [Message search] ===");
        Message checkMessage =
                messageService.getAllMessages().stream()
                        .filter(m -> m.getSender().getUsername().equalsIgnoreCase("Kim"))
                        .findFirst()
                        .orElse(null);

        if(checkMessage != null) {
            System.out.println("Find message successfully.");
            System.out.println("Channel : " + checkMessage.getChannel().getName());
            System.out.println("Sender : " + checkMessage.getSender().getUsername());
            System.out.println("Message : " + checkMessage.getContent());
        } else {
            System.out.println("Error: Kim's message not found.");
        }

        // 다건 조희(User)
        System.out.println("\n=== [Searh all users] ===");
        userService.getAllUsers().forEach(user ->
                System.out.println("User: " + user.getUsername() + " - " + user.getEmail())
        );
        // 다건 조회(Channel)
        System.out.println("\n=== [Search all channels] ===");
        channelService.getAllChannels().forEach(channel ->
                System.out.println("Channel: " + channel.getName())
        );

        // 다건 조회(Message)
        System.out.println("\n=== [Search all Messages] ===");
        messageService.getAllMessages().forEach(message ->
                System.out.println("Message: " + message.getContent()
                        + " | Sender: " + message.getSender().getUsername()
                        + " | Channel: " + message.getChannel().getName())
        );

        // User 수정: "Kim"를 "KimUpdated"로 변경
        System.out.println("\n=== [User Edit] ===");
        if (Kim != null) {
            System.out.println("[Before]: " + Kim.getUsername() + " + " + Kim.getEmail());
            userService.updateUser(Kim.getId(), "KimUpdated", "upKim@google.com");
            // 수정된 데이터 조회 (단건 조회)
            User updatedKim = userService.getUserById(Kim.getId());
            System.out.println("[After]: " + updatedKim.getUsername()
                    + " - " + updatedKim.getEmail());
        } else {
            System.out.println("User to update not found.");
        }

        // Channel 삭제: "First" 채널 삭제
        System.out.println("\n=== [Channel delete] ===");
        if (FirstChannel != null) {
            channelService.deleteChannel(FirstChannel.getId());
            System.out.println("Channel 'First' deleted successfully.");
        } else {
            System.out.println("Channel 'First' not found.");
        }

        // 삭제 확인: 모든 채널 조회하여 "First" 채널이 없는지 확인
        System.out.println("\n=== [Delete confirm: View all channels] ===");
        channelService.getAllChannels().forEach(channel ->
                System.out.println("Channel: " + channel.getName())
        );

    }
}