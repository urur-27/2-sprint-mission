package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 등록, 조회(단건, 다건), 수정, 수정된 데이터 조회, 삭제, 조회를 통해 삭제되었는지 확인
        // switch - case문을 이용해서 동작하는 서비스 설계
        UserService userService = ServiceFactory.getInstance().getUserService();
        ChannelService channelService = ServiceFactory.getInstance().getChannelService();
        MessageService messageService = ServiceFactory.getInstance().getMessageService();

        // 숫자를 입력받아 작동시키기
        Scanner sc = new Scanner(System.in);
        boolean run = true;

        // 메뉴를 선택하는 while
        while (run) {
            System.out.println("\n=== Main Menu ===");
            System.out.println("1. User");
            System.out.println("2. Message");
            System.out.println("3. Channel");
            System.out.println("4. Exit");

            int choice = getValidatedInt(sc, "Choice: ");

            switch (choice) {
                case 1: // User 메뉴
                    userMenu(userService, sc);
                    break;
                case 2: // Message 메뉴
                    messageMenu(messageService, userService, channelService, sc);
                    break;
                case 3: // Channel 메뉴
                    channelMenu(channelService, sc);
                    break;
                case 4: // 종료
                    run = false;
                    System.out.println("[Info] Exit the program.");
                    break;
                default:
                    System.out.println("[Error] Invalid input..");
            }
        }

        sc.close();
    }

    // 1. User 메뉴
    private static void userMenu(UserService userService, Scanner sc) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Register User");
            System.out.println("2. Search User");
            System.out.println("3. See All User");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Back to Main Menu");

            int choice = getValidatedInt(sc, "Choice: ");
            switch (choice) {
                case 1: // 유저 등록
                    // 이름과 이메일을 받아서 등록
                    createUser(userService, sc);
                    break;
                case 2: // 유저 검색. 이름을 기반으로 해당 유저를 검색
                    printUserByUsername(userService, sc);
                    break;
                case 3: // 모든 유저 검색
                    searchAllUsers(userService);
                    break;
                case 4: // 유저 수정. 수정할 유저 이름을 기반으로 검색 후 업데이트
                    updateUser(userService, sc);
                    break;
                case 5: // 유저 삭제
                    deleteUser(userService, sc);
                    break;
                case 6: // 메인 메뉴로 돌아가기
                    System.out.println("[Info] return to Main Menu.");
                    return;
                default:
                    System.out.println("[Error] Invalid input.");
            }
        }
    }

    // 2. Message 메뉴
    private static void messageMenu(MessageService messageService, UserService userService, ChannelService channelService, Scanner sc) {
        while (true) {
            System.out.println("\n=== Message Menu ===");
            System.out.println("1. Send Message");
            System.out.println("2. Search Message");
            System.out.println("3. View All Messages");
            System.out.println("4. Update Message");
            System.out.println("5. Delete Message");
            System.out.println("6. Back to Main Menu");

            int choice = getValidatedInt(sc, "Choice: ");

            switch (choice) {
                case 1: // 메시지 전송. 유저 이름, 채널 이름, 메시지를 받아서 유저와 채널이 실존하면 메시지 전송
                    createMessage(messageService, userService, channelService, sc);
                    break;
                case 2: // 메시지 조회. 채널, 작성자를 검색하여 조회 가능하도록 설계
                    searchMessage(messageService, sc);
                    break;
                case 3: //모든 메시지 출력
                    searchAllMessages(messageService);
                    break;
                case 4: // 메시지 수정
                    updateMessage(messageService, sc);
                    break;
                case 5: // 메시지 삭제. 어느 채널에 작성된 메시지인지 보여주고 선택하여 삭제
                    deleteMessage(messageService, sc);
                    break;
                case 6: // 메인 메뉴로 돌아가기
                    System.out.println("[Info] return to Main Menu.");
                    return;

                default:
                    System.out.println("[Error] Invalid input.");
            }
        }
    }

    // 3. Channel 메뉴
    private static void channelMenu(ChannelService channelService, Scanner sc) {
        while (true) {
            System.out.println("\n=== Channel Menu ===");
            System.out.println("1. Create Channel");
            System.out.println("2. Search Channel");
            System.out.println("3. See All Channels");
            System.out.println("4. Update Channel");
            System.out.println("5. Delete Channel");
            System.out.println("6. Back to Main Menu");

            int choice = getValidatedInt(sc, "Choice: ");

            switch (choice) {
                case 1: // 채널 생성
                    createChannel(channelService, sc);
                    break;
                case 2: // 채널 검색
                    searchChannel(channelService, sc);
                    break;
                case 3: // 모든 채널 조회
                   searchAllChannels(channelService);
                   break;
                case 4: // 채널 수정
                    updateChannel(channelService, sc);
                    break;
                case 5: // 채널 삭제. 임의의 값을 이용하여 채널 삭제 진행
                    deleteChannel(channelService, sc);
                    break;
                case 6:
                    System.out.println("[Info] return to Main Menu.");
                    return;
                default:
                    System.out.println("[Error] Invalid input.");
            }
        }
    }

    // 모든 유저 출력
    private static void searchAllUsers(UserService userService) {
        System.out.println("\n=== User List ===");
        userService.findAll().forEach(user -> System.out.println("UserName: "+user.getUsername() + " | Email: " + user.getEmail()));
    }

    // 모든 채널 출력
    private static void searchAllChannels(ChannelService channelService) {
        System.out.println("\n=== Channel List ===");
        channelService.findAll().forEach(channel -> System.out.println("ChannelName: "+channel.getName()));
    }

    // 모든 메시지 출력
    private static void searchAllMessages(MessageService messageService) {
        System.out.println("\n=== Message List ===");
        messageService.findAll().forEach(message ->
                System.out.println("[" + message.getSender().getUsername() + "] " + message.getContent()));
    }

    // 이름으로 유저 검색
    private static void printUserByUsername(UserService userService, Scanner sc) {
        System.out.print("Enter User Name to search: ");
        String userName = sc.nextLine();

        userService.findAll().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(userName))
                .forEach(user -> System.out.println("[Info] User found. User: " + user.getUsername() + " | Email: " + user.getEmail()));
    }

    // 메시지 검색 (채널명 혹은 보낸이로 검색)
    private static void searchMessage(MessageService messageService, Scanner sc) {
        System.out.print("Enter Sender Name or Channel Name to search messages: ");
        String keyword = sc.nextLine().toLowerCase();

        List<Message> messages = messageService.findAll().stream()
                .filter(msg -> msg.getSender().getUsername().equalsIgnoreCase(keyword)
                        || msg.getChannel().getName().equalsIgnoreCase(keyword))
                .toList();

        if (messages.isEmpty()) {
            System.out.println("[Info] No messages found.");
        } else {
            System.out.println("\n=== Search Results ===");
            messages.forEach(msg -> System.out.println("[" + msg.getSender().getUsername() + "] " + msg.getContent()));
        }
    }

    // 채널 검색
    private static void searchChannel(ChannelService channelService, Scanner sc) {
        System.out.print("Enter Channel Name to search: ");
        String channelName = sc.nextLine();

        channelService.findAll().stream()
                .filter(channel -> channel.getName().equalsIgnoreCase(channelName))
                .findFirst()
                .ifPresentOrElse(
                        channel -> System.out.println("[Info] Channel found: " + channel.getName()),
                        () -> System.out.println("[Error] Channel not found.")
                );
    }

    // 유저 정보 수정
    private static void updateUser(UserService userService, Scanner sc) {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            System.out.println("[Info] No users available to update.");
            return;
        }

        System.out.println("\n=== Select a User to Update ===");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". [" + user.getUsername() + "] - " + user.getEmail());
        }

        int updateIndex = getValidatedInt(sc,"Enter the user number to update: ");


        if (updateIndex < 1 || updateIndex > users.size()) {
            System.out.println("[Error] Invalid user number.");
        } else {
            User toUpdate = users.get(updateIndex - 1);
            String oldUsername = toUpdate.getUsername();
            String oldEmail = toUpdate.getEmail();
            System.out.print("Enter new Username: ");
            String newUsername = sc.nextLine();
            System.out.print("Enter new Email: ");
            String newEmail = sc.nextLine();

            userService.update(toUpdate.getId(), newUsername, newEmail);
            System.out.println("[Info] Username: " + oldUsername + " -> " + newUsername
            + " | Email: "+ oldEmail +" -> "+newEmail);
        }
    }

    // 채널 수정
    private static void updateChannel(ChannelService channelService, Scanner sc) {
        List<Channel> channels = channelService.findAll();

        if (channels.isEmpty()) {
            System.out.println("[Info] No channels available to update.");
            return;
        }

        System.out.println("\n=== Select a Channel to Update ===");
        for (int i = 0; i < channels.size(); i++) {
            Channel ch = channels.get(i);
            System.out.println((i + 1) + ". [" + ch.getName() + "]");
        }

        int updateIndex = getValidatedInt(sc,"Enter the channel number to update: ");
        if (updateIndex < 1 || updateIndex > channels.size()) {
            System.out.println("[Error] Invalid channel number.");
        } else {
            Channel toUpdate = channels.get(updateIndex - 1);
            String oldChannelName = toUpdate.getName();
            System.out.print("Enter new Channel Name: ");
            String newChannelName = sc.nextLine();
            channelService.update(toUpdate.getId(), newChannelName);
            System.out.println("[Info] Channel updated: " + oldChannelName + " -> " + newChannelName);
        }
    }

    // 메시지 수정
    private static void updateMessage(MessageService messageService, Scanner sc) {
        List<Message> messages = messageService.findAll();

        if (messages.isEmpty()) {
            System.out.println("[Info] No messages available to update.");
            return;
        }

        System.out.println("\n=== Select a Message to Update ===");
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            System.out.println((i + 1) + ". [" + msg.getSender().getUsername() + "] " + msg.getContent());
        }

        int updateIndex = getValidatedInt(sc,"Enter the message number to update: ");
        if (updateIndex < 1 || updateIndex > messages.size()) {
            System.out.println("[Error] Invalid message number.");
        } else {
            Message toUpdate = messages.get(updateIndex - 1);
            String oldMessageContent = toUpdate.getContent();
            System.out.print("Enter new Message Content: ");
            String newMessageContent = sc.nextLine();
            messageService.update(toUpdate.getId(), newMessageContent);
            System.out.println("[Info] Message updated: " + oldMessageContent + " -> " + newMessageContent);
        }
    }

    // 유저 삭제
    private static void deleteUser(UserService userService, Scanner sc) {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            System.out.println("[Info] No users available to delete.");
            return;
        }

        System.out.println("\n=== Select a User to Delete ===");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". [" + user.getUsername() + "] - " + user.getEmail());
        }

        int deleteIndex = getValidatedInt(sc,"Enter the user number to delete: ");
        if (deleteIndex < 1 || deleteIndex > users.size()) {
            System.out.println("[Error] Invalid user number.");
        } else {
            User toDelete = users.get(deleteIndex - 1);
            userService.delete(toDelete.getId());
            System.out.println("[Info] User deleted: " + toDelete.getUsername());
        }
    }

    // 메시지 삭제
    private static void deleteMessage(MessageService messageService, Scanner sc) {
        List<Message> messages = messageService.findAll();

        if (messages.isEmpty()) {
            System.out.println("[Info] No messages available to delete.");
            return;
        }

        System.out.println("\n=== Select a Message to Delete ===");
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            System.out.println((i + 1) + ". [" + msg.getSender().getUsername() + "] " + msg.getContent());
        }

        int deleteIndex = getValidatedInt(sc,"Enter the message number to delete: ");

        if (deleteIndex < 1 || deleteIndex > messages.size()) {
            System.out.println("[Error] Invalid message number.");
        } else {
            Message toDelete = messages.get(deleteIndex - 1);
            messageService.delete(toDelete.getId());
            System.out.println("[Info] Message deleted: " + toDelete.getContent());
        }
    }

    // 채널 삭제
    private static void deleteChannel(ChannelService channelService, Scanner sc) {
        List<Channel> channels = channelService.findAll();

        if (channels.isEmpty()) {
            System.out.println("[Info] No channels available to delete.");
            return;
        }

        System.out.println("\n=== Select a Channel to Delete ===");
        for (int i = 0; i < channels.size(); i++) {
            Channel ch = channels.get(i);
            System.out.println((i + 1) + ". [" + ch.getName() + "]");
        }

        int deleteIndex = getValidatedInt(sc,"Enter the channel number to delete: ");
        if (deleteIndex < 1 || deleteIndex > channels.size()) {
            System.out.println("[Error] Invalid channel number.");
        } else {
            Channel toDelete = channels.get(deleteIndex - 1);
            channelService.delete(toDelete.getId());
            System.out.println("[Info] Channel deleted: " + toDelete.getName());
        }
    }

    // 유저 생성
    private static void createUser(UserService userService, Scanner sc){
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        userService.create(username, email);
        // 등록된 유저 정보 출력
        System.out.println("[Info] Username : " + username + ", Email : "+email+" is registered.");
    }

    // 채널 생성
    private static void createChannel(ChannelService channelService, Scanner sc){
        System.out.print("Enter Channel Name: ");
        String channelName = sc.nextLine();
        channelService.create(channelName);
        System.out.println("[Info] Channel ["+channelName+"] created successfully.");
    }

    // 메시지 생성(전송)
    private static void createMessage(MessageService messageService, UserService userService, ChannelService channelService, Scanner sc) {
        System.out.print("Enter Sender Username: ");
        String senderName = sc.nextLine();
        System.out.print("Enter Channel Name: ");
        String channelName = sc.nextLine();
        System.out.print("Enter Message: ");
        String content = sc.nextLine();

        User sender = userService.findAll().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(senderName))
                .findFirst().orElse(null);
        Channel channel = channelService.findAll().stream()
                .filter(ch -> ch.getName().equalsIgnoreCase(channelName))
                .findFirst().orElse(null);

        if (sender != null && channel != null) {
            messageService.create(content, sender.getId(), channel.getId());
            System.out.println("[Info] Message sent: " + content);
        } else {
            System.out.println("[Error] Sender or Channel not found.");
        }
    }


    // 숫자가 입력 되었는지 확인
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);  // 문자열을 Double로 변환을 시도
            return true;  // 변환 성공 시 숫자
        } catch (NumberFormatException e) {
            return false;  // 변환 실패 시 숫자가 아님
        }
    }

    // 입력 받는 내용이 숫자인지 검증하여 맞으면 int 값 반환
    public static int getValidatedInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();
            if (isNumeric(input)) {
                return Integer.parseInt(input);
            } else {
                System.out.println("[Error]: Please enter a valid number.");
            }
        }
    }

}