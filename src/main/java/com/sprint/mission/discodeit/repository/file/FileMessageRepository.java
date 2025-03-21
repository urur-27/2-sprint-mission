package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileMessageRepository implements MessageRepository, FileRepository {
    private static final Path MESSAGE_DIR = Paths.get("output/messagedata");

    public FileMessageRepository() {
        try {
            createDirectories(MESSAGE_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory", e);
        }
    }

    // UUID에 대응하는 객체 리턴
    private Path getMessageFile(UUID id) {
        return MESSAGE_DIR.resolve(id.toString() + ".dat");
    }

    // 파일 저장을 위한 경로
    @Override
    public void createDirectories(Path path) throws IOException {
        if (Files.exists(path) == false) {
            Files.createDirectories(path);
        }
    }

    // 파일 쓰기
    @Override
    public void writeFile(Path path, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        }
    }

    // 파일 읽어오기
    @Override
    public <T> T readFile(Path path, Class<T> clazz) throws IOException, ClassNotFoundException {
        if (Files.exists(path) == false) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return clazz.cast(ois.readObject());
        }
    }

    // Message 객체를 해당 파일에 직렬화하여 저장
    @Override
    public void upsert(Message message) {
        Path filePath = getMessageFile(message.getId());
        try {
            writeFile(filePath, message);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upsert message", e);
        }
    }

    // 파일에서 Message 객체를 역직렬화하여 읽어옴
    @Override
    public Message findById(UUID id) {
        Path filePath = getMessageFile(id);
        try {
            return readFile(filePath, Message.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find message", e);
        }
    }

    public List<Message> findAll() {
        File[] files = MESSAGE_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<Message> result = new ArrayList<>();
        for (File f : files) {
            try {
                result.add(readFile(f.toPath(), Message.class));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to find all message", e);
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
        Path filePath = getMessageFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete channel file", e);
        }
    }
}
