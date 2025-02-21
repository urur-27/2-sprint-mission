package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Scanner;

public class Main {
    // 메인 메소드가 선언된 클래스
    public static void main(String[] args) {
        // 등록, 조회(단건, 다건), 수정, 수정된 데이터 조회, 삭제, 조회를 통해 삭제되었는지 확인
        // switch - case문을 이용해서 동작하는 서비스 설계

        //서비스 객체 생성
        JCFChannelService channelService = new JCFChannelService();
        JCFUserService userService = new JCFUserService();
        JCFMessageService messageService = new JCFMessageService();

        // 숫자를 입력받아 작동시키기
        Scanner sc = new Scanner(System.in);
        boolean run = true;

        // 메뉴를 선택하는 while
        while (run) {
            System.out.println("\n=== Menu ===");
            System.out.println("1. User");
            System.out.println("2. Message");
            System.out.println("3. Channel");
            System.out.println("4. Exit");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

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
    private static void userMenu(JCFUserService userService, Scanner sc) {
        while (true) {
            System.out.println("\n=== User Menu ===");
            System.out.println("1. Register User");
            System.out.println("2. Search User");
            System.out.println("3. Search All User");
            System.out.println("4. Update User");
            System.out.println("5. Delete User");
            System.out.println("6. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: // 유저 등록
                    // 이름과 이메일을 받아서 등록
                    System.out.print("Enter Username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter Email: ");
                    String email = sc.nextLine();
                    userService.createUser(username, email);
                    // 등록된 유저 정보 출력
                    System.out.print("[Info] Username : " + username + ", Email : "+email+" is registered.");
//                    System.out.println("[Info] User registered successfully.");
                    break;

                case 2: // 유저 검색. 이름을 기반으로 해당 유저를 검색
                    System.out.print("Enter Username to search: ");
                    String searchUsername = sc.nextLine();
                    // steram API 이용. userService에서 모든 user 정보를 가져오고 거기서 현재 입력된 이름과 같은 이름이 있는지 검색하여 출력
                    userService.getAllUsers().stream()
                            .filter(user -> user.getUsername().equalsIgnoreCase(searchUsername)) // equalsIgnoreCase를 사용하여 대소문자 구분 없이 비교
                            .forEach(user -> System.out.println("User Found: " + user.getUsername() + " - " + user.getEmail()));
                    break;

                case 3: // 모든 유저 검색
                    System.out.print("=== Search All Users(userName - userEmail) ===\n");
                    //forEach를 통해 모든 유저 검색
                    userService.getAllUsers()
                            .forEach(user -> System.out.println(user.getUsername() + " - " + user.getEmail()));
                    break;


                case 4: // 유저 수정. 수정할 유저 이름을 기반으로 검색 후 업데이트
                    System.out.print("Enter Username to update: ");
                    String oldUsername = sc.nextLine();
                    // list로 모든 유저들 정보를 받아두고 비교
                    // switch-case는 {}을 사용하지 않으면 switch 블록 내부에서 전역 변수처럼 취급. 따라서 case5에서도 users 변수 사용 가능
                    List<User> users = userService.getAllUsers();
                    for (User user : users) {
                        if (user.getUsername().equalsIgnoreCase(oldUsername)) {
                            System.out.print("Enter New Username: ");
                            String newUsername = sc.nextLine();
                            System.out.print("Enter New Email: ");
                            String newEmail = sc.nextLine();

                            System.out.println("[Info] Username: "+oldUsername+" -> "+newUsername);
                            System.out.println("[Info] Email: "+user.getEmail()+" -> "+newEmail);
                            userService.updateUser(user.getId(), newUsername, newEmail);
//                            System.out.println("[Info] User updated successfully.");
                            return;
                        }
                    }
                    System.out.println("[Error] User not found.");
                    break;

                case 5: // 유저 삭제
                    System.out.print("Enter Username to delete: ");
                    String deleteUsername = sc.nextLine();
                    users = userService.getAllUsers();
                    for (User user : users) {
                        if (user.getUsername().equalsIgnoreCase(deleteUsername)) {
                            userService.deleteUser(user.getId());
                            System.out.println("[Info] User \""+deleteUsername+"\" deleted successfully");
//                            System.out.println("[Info] User deleted successfully.");
                            return;
                        }
                    }
                    System.out.println("[Error] User not found.");
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
    private static void messageMenu(JCFMessageService messageService, JCFUserService userService, JCFChannelService channelService, Scanner sc) {
        while (true) {
            System.out.println("\n=== Message Menu ===");
            System.out.println("1. Send Message");
            System.out.println("2. View Messages");
            System.out.println("3. Delete Message");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: // 메시지 전송. 유저 이름, 채널 이름, 메시지를 받아서 유저와 채널이 실존하면 메시지 전송
                    System.out.print("Enter Sender Username: ");
                    String senderName = sc.nextLine();
                    System.out.print("Enter Channel Name: ");
                    String channelName = sc.nextLine();
                    System.out.print("Enter Message: ");
                    String content = sc.nextLine();

                    User sender = userService.getAllUsers().stream()
                            .filter(user -> user.getUsername().equalsIgnoreCase(senderName))
                            .findFirst().orElse(null); // 값이 하나도 없는 경우 null로 예외처리
                    Channel channel = channelService.getAllChannels().stream()
                            .filter(ch -> ch.getName().equalsIgnoreCase(channelName))
                            .findFirst().orElse(null);

                    if (sender != null && channel != null) {
                        messageService.createMessage(content, sender, channel);
                        System.out.println("[Info] Message sent successfully.");
                        System.out.println("[Info] Channel: "+channel.getName());
                        System.out.println("[Info] Sender: "+sender.getUsername());
                        System.out.println("[Info] Message content: "+content);
                    } else {
                        System.out.println("[Error] Sender or Channel not found."); // 유저 이름이나 채널명이 없는 경우 예외처리
                    }
                    break;

                case 2: // 메시지 조회. 채널, 작성자를 검색하여 조회 가능하도록 설계(추가해야함)
                    //모든 메시지 출력
                    List<Message> messages = messageService.getAllMessages(); // 메시지에 인덱스 번호를 입력해주기 위해

                    if (messages.isEmpty()) {
                        System.out.println("[Info] No messages found."); // 메시지가 없는 경우
                    } else {
                        System.out.println("\n=== Message List ===");
                        for (int i = 0; i < messages.size(); i++) {
                            Message msg = messages.get(i);
                            System.out.println(i + 1 + ". [" + msg.getSender().getUsername() + "] " + msg.getContent());
                        }
                    }
                    break;

                case 3: // 메시지 삭제. 어느 채널에 작성된 메시지인지 보여주고 선택하여 삭제
                    messages = messageService.getAllMessages(); // 모든 메시지를 저장

                    if (messages.isEmpty()) {
                        System.out.println("[Info] There is no message");
                        break;
                    }

                    // 메시지에 번호를 붙여서 출력
                    System.out.println("\n=== Select a Message to Delete ===");
                    for (int i = 0; i < messages.size(); i++) {
                        Message msg = messages.get(i);
                        System.out.println(i + 1 + ". [" + msg.getSender().getUsername() + "] " + msg.getContent());
                    }

                    System.out.print("Enter the message number to delete: ");
                    int deleteIndex = sc.nextInt();
                    sc.nextLine();

                    if (deleteIndex < 1 || deleteIndex > messages.size()) {
                        System.out.println("[Error] Invalid message number.");
                    } else {
                        Message toDelete = messages.get(deleteIndex - 1);
                        messageService.deleteMessage(toDelete.getId());
                        System.out.println("[Info] Message deleted successfully.");
                    }
                    break;

                case 4: // 메인 메뉴로 돌아가기
                    return;

                default:
                    System.out.println("[Error] Invalid input.");
            }
        }
    }

    // 3. Channel 메뉴
    private static void channelMenu(JCFChannelService channelService, Scanner sc) {
        while (true) {
            System.out.println("\n=== Channel Menu ===");
            System.out.println("1. Create Channel");
            System.out.println("2. View Channels");
            System.out.println("3. Delete Channel");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: // 채널 생성
                    System.out.print("Enter Channel Name: ");
                    String channelName = sc.nextLine();
                    channelService.createChannel(channelName);
                    System.out.println("[Info] Channel ["+channelName+"] created successfully.");
                    break;

                case 2: // 모든 채널 조회
                    List<Channel> channels = channelService.getAllChannels();

                    if (channels.isEmpty()) {
                        System.out.println("[Info] No messages found."); // 채널이 없는 경우
                        break;
                    } else {
                        System.out.println("\n=== Channel List ===");
                        for (int i = 0; i < channels.size(); i++) {
                            Channel ch = channels.get(i);
                            System.out.println(i + 1 + ". [" + ch.getName() + "] ");
                        }
                    }
//                    channelService.getAllChannels().forEach(channel -> System.out.println("Channel: " + channel.getName()));
                    break;

                case 3: // 채널 삭제. 임의의 값을 이용하여 채널 삭제 진행
                    channels = channelService.getAllChannels();

                    if (channels.isEmpty()) {
                        System.out.println("[Info] No messages found.");
                        break;
                    }

                    // 체널이 비어있지 않은 경우 진행. 삭제 진행을 위해 현재 존재하는 채널의 보기를 제시
                    System.out.println("\n=== Select a Channel to Delete ===");
                    for (int i = 0; i < channels.size(); i++) {
                        Channel ch = channels.get(i);
                        System.out.println(i + 1 + ". [" + ch.getName() + "] ");
                    }

                    // 채널의 번호를 선택하여 삭제 진행
                    System.out.print("Enter the message number to delete: ");
                    int deleteIndex = sc.nextInt();
                    sc.nextLine();

                    if (deleteIndex < 1 || deleteIndex > channels.size()) {
                        System.out.println("[Error] Invalid channel number.");
                    } else {
                        Channel toDelete = channels.get(deleteIndex - 1);
                        channelService.deleteChannel(toDelete.getId());
                        System.out.println("[Info] Channel deleted successfully.");
                    }
                    break;
                case 4:
                    return;

                default:
                    System.out.println("[Error] Invalid input.");
            }
        }
    }
}
