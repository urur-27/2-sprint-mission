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
        userService.getAllUsers().forEach(user ->
                System.out.println("User: " + user.getUsername() + " - " + user.getEmail()));


        // 등록(Channel). First, Second
        System.out.println("\n=== [Channel create] ===");
        channelService.createChannel("First");
        channelService.createChannel("Second");
        channelService.getAllChannels().forEach(channel ->
                System.out.println("Channel: " + channel.getName()));

        // 단건 조회(User). Kim 조회
        System.out.println("\n=== [User search] ===");

        // 이런 식으로 조회한다면 getUserById가 쓸모 없는 게 아닌가?
        User kim = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase("Kim"))
                .findFirst()
                .orElse(null); // Kim 정보 받아오기. 없으면 null

        // 정상적으로 조회가 되었는지 null 여부 판단.
        if (kim != null) {
            System.out.println("User found: " + kim.getUsername());
        } else {
            System.out.println("Error: Kim not found.");
        }

        // 단건 조회(Channel). First 조회
        System.out.println("\n=== [Channel search] ===");
        Channel firstChannel =
                channelService.getAllChannels().stream()
                        .filter(ch -> ch.getName().equalsIgnoreCase("First"))
                        .findFirst()
                        .orElse(null); // First 정보 받아오기

        // 정상적으로 조회가 되었는지 판단
        if (firstChannel != null) {
            System.out.println("Channel found: " + firstChannel.getName());
        } else {
            System.out.println("Error: First channel not found.");
        }

        // 등록(Message). Kim이 First채널에서 보내는 Message
        System.out.println("\n=== [Message create] ===");
        //Kim 과 First 채널이 존재하는 경우 메시지 생성
        if (kim != null && firstChannel != null) {
            messageService.createMessage("First channel message - by Kim", kim.getId(), firstChannel.getId());
            System.out.println("Message sent: First channel message - by Kim");
        } else {
            System.out.println("Error: Message creation failed due to missing user or channel.");
        }


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
        if (min != null && secondChannel != null){
            messageService.createMessage("Second channel message - by min", min.getId(), secondChannel.getId());
            System.out.println("Message sent: Second channel message - by min");
        } else {
            System.out.println("Error: Message creation failed due to missing user or channel.");
        }

        // 단건 조회(Message). Kim이 작성한 메시지 조회
        System.out.println("\n=== [Message search] ===");
        Message checkMessage =
                messageService.getAllMessages().stream()
                        .filter(m -> m.getSender().getUsername().equalsIgnoreCase("Kim"))
                        .findFirst()
                        .orElse(null);

        if(checkMessage != null) {
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
        System.out.println("\n=== [User Update] ===");
        if (kim != null) {
            System.out.println("[Before]: " + kim.getUsername() + " - " + kim.getEmail());
            userService.updateUser(kim.getId(), "KimUpdated", "upKim@google.com");
            // 수정된 데이터 조회 (단건 조회)
            User updatedKim = userService.getUserById(kim.getId());
            System.out.println("[After]: " + updatedKim.getUsername()
                    + " - " + updatedKim.getEmail());
        } else {
            System.out.println("User to update not found.");
        }

        // Message 수정. Kim이 작성한 메시지 수정
        System.out.println("\n=== [Message Update] ===");
        if (checkMessage != null) {
            System.out.println("[Before]: " + checkMessage.getContent());
            messageService.updateMessage(checkMessage.getId(), "Updated message by Kim");
            Message updatedMessage = messageService.getMessageById(checkMessage.getId());
            System.out.println("[After]: " + updatedMessage.getContent());
        } else {
            System.out.println("Message to update not found.");
        }

        // Channel 수정. First 채널 이름 수정
        System.out.println("\n=== [Channel Update] ===");
        if (firstChannel != null) {
            System.out.println("[Before]: " + firstChannel.getName());
            channelService.updateChannel(firstChannel.getId(), "Updated First Channel");
        Channel updatedChannel = channelService.getChannelById(firstChannel.getId());
        System.out.println("[After]: " + updatedChannel.getName());
        } else {
            System.out.println("Channel to update not found.");
        }




        // User 삭제. Kim 삭제
        System.out.println("\n=== [User Delete] ===");
        userService.deleteUser(kim.getId());
        User deletedUser = userService.getUserById(kim.getId());
        if (deletedUser == null) {
            System.out.println("User deleted successfully.");
        } else {
            System.out.println("Error: User deletion failed.");
        }
        System.out.println("view All users:");
        userService.getAllUsers().forEach(user ->
                System.out.println("User: " + user.getUsername() + " - " + user.getEmail()));

        // Channel 삭제: "First" 채널 삭제
        System.out.println("\n=== [Channel delete] ===");
        if (firstChannel != null) {
            channelService.deleteChannel(firstChannel.getId());
            System.out.println("Channel '"+firstChannel.getName()+"' deleted successfully.");
        } else {
            System.out.println("Channel 'First' not found.");
        }
        System.out.println("View all channels:");
        channelService.getAllChannels().forEach(channel ->
                System.out.println("Channel: " + channel.getName()));

        // Message 삭제. Kim이 작성한 메시지 삭제
        System.out.println("\n=== [Message Delete] ===");
        messageService.deleteMessage(checkMessage.getId());
        System.out.println("View all messages:");
        messageService.getAllMessages().forEach(message ->
                System.out.println("Message: " + message.getContent()
                        + " | Sender: " + message.getSender().getUsername()
                        + " | Channel: " + message.getChannel().getName()));


    }
}