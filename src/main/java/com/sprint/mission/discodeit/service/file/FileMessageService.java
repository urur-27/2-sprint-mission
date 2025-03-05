package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    // message를 저장할 디렉토리(상대 경로)
    private static final File MESSAGE_DIR = new File("output/messagedata");

    // 싱글턴 패턴
    private static volatile FileMessageService instance;

    private final UserService userService;
    private final ChannelService channelService;

    private FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        // 생성자에서 디렉토리가 없는 경우 생성
        if (!MESSAGE_DIR.exists()) {
            MESSAGE_DIR.mkdirs();
        }
    }

    public static FileMessageService getInstance(UserService userService, ChannelService channelService) {
        if (instance == null) {
            synchronized (FileMessageService.class) {
                if (instance == null) {
                    instance = new FileMessageService(userService, channelService);
                }
            }
        }
        return instance;
    }

    // 특정 UUID에 대응하는 파일 객체 리턴.
    // 예: output/message_data/123e4567-e89b-12d3-a456-426614174000.dat
    private File getMessageFile(UUID id) {
        return new File(MESSAGE_DIR, id.toString() + ".dat");
    }

    // Message 객체를 해당 파일(output/message_data/{UUID}.dat)에 직렬화하여 저장
    private void saveMessageToFile(Message message) {
        // message.getId()를 통하여 파일명 가져오기
        File f = getMessageFile(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 Message 객체를 역직렬화하여 읽어옴
    private Message loadMessageFromFile(UUID id) {
        File f = getMessageFile(id);
        if (!f.exists()) {
            // 해당 id의 파일이 없는 경우 return null
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            // Message로 캐스팅하여 리턴
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------
    //  CRUD 구현
    // -----------------------

    @Override
    public UUID createMessage(String content, UUID senderId, UUID channelId) {
        User sender = findUserById(senderId);
        Channel channel = findChannelById(channelId);

        Message message = new Message(content, sender, channel);
        // Message를 개별 파일에 직렬화 저장
        saveMessageToFile(message);
        return message.getId();
    }


    @Override
    public Message getMessageById(UUID id) {
        return Optional.ofNullable(loadMessageFromFile(id))
                .orElseThrow(() -> new NoSuchElementException("No message found for ID: " + id));
//        Message message = loadMessageFromFile(id);
//        if (message == null) {
//            // 해당 id의 Message 파일이 없다면 예외처리
//            throw new NoSuchElementException("No message file found for ID: " + id);
//        }
//        return message;
    }

    @Override
    public List<Message> getAllMessages() {
        File[] files = MESSAGE_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            // 폴더가 존재하지 않거나 IO 에러 등
            return new ArrayList<>();
        }

        List<Message> result = new ArrayList<>();
        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Message message = (Message) ois.readObject();
                result.add(message);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void updateMessage(UUID id, String content) {
        Message message = loadMessageFromFile(id);
        if (message == null) {
            throw new NoSuchElementException("No message file found for ID: " + id);
        }
        message.updateMessage(content);
        // 수정 후 다시 저장
        saveMessageToFile(message);
    }

    @Override
    public void deleteMessage(UUID id) {
        File f = getMessageFile(id);
        if (!f.exists() || !f.isFile()) {
            throw new NoSuchElementException("No message file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (!deleted) {
            throw new RuntimeException("Failed to delete message file for ID: " + id);
        }
    }

    // User ID 검증
    private User findUserById(UUID id) {
        return Optional.ofNullable(userService.getUserById(id))
                .orElseThrow(() -> new NoSuchElementException("User does not exist: " + id));
    }

    // Channel ID 검증
    private Channel findChannelById(UUID id) {
        return Optional.ofNullable(channelService.getChannelById(id))
                .orElseThrow(() -> new NoSuchElementException("Channel does not exist: " + id));
    }
}
