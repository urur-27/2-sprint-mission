package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final File MESSAGE_DIR = new File("output/messagedata");

    public FileMessageRepository() {
        if (MESSAGE_DIR.exists() == false) {
            MESSAGE_DIR.mkdirs();
        }
    }

    // UUID에 대응하는 객체 리턴
    private File getMessageFile(UUID id) {
        return new File(MESSAGE_DIR, id.toString() + ".dat");
    }

    // Message 객체를 해당 파일에 직렬화하여 저장
    @Override
    public void upsert(Message message) {
        File f = getMessageFile(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 Message 객체를 역직렬화하여 읽어옴
    @Override
    public Message findById(UUID id) {
        File f = getMessageFile(id);
        if (f.exists() == false) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Message> findAll() {
        File[] files = MESSAGE_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
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

    // 사용자 정보 업데이트
    @Override
    public void update(UUID id, String newMessageName) {
        Message message = findById(id);
        if (message == null) {
            throw new NoSuchElementException("No message file found for ID: " + id);
        }
        message.updateMessage(newMessageName);
        upsert(message);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        File f = getMessageFile(id);
        if (f.exists() == false || f.isFile() == false) {
            throw new NoSuchElementException("No message file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (deleted == false) {
            throw new RuntimeException("Failed to delete message file for ID: " + id);
        }
    }

}
