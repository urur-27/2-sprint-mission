package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.sprint.mission.discodeit.common.CodeitConstants.FILE_EXTENSION;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileMessageRepository implements MessageRepository, FileRepository {
    private final Path MESSAGE_DIR;

    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.MESSAGE_DIR = Paths.get(fileDirectory, "messagedata");
        createDirectories(MESSAGE_DIR);
    }

    // UUID에 대응하는 객체 리턴
    private Path getMessageFile(UUID id) {
        return MESSAGE_DIR.resolve(id.toString() + FILE_EXTENSION);
    }

    // 파일 저장을 위한 경로
    @Override
    public void createDirectories(Path path) {
        try {
            if (Files.exists(path) == false) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directories: " + path, e);
        }
    }

    // 파일 쓰기
    @Override
    public void writeFile(Path path, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    // 파일 읽어오기
    @Override
    public <T> T readFile(Path path, Class<T> clazz) {
        if (Files.exists(path) == false) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    // Message 객체를 해당 파일에 직렬화하여 저장
    @Override
    public void upsert(Message message) {
        Path filePath = getMessageFile(message.getId());
        writeFile(filePath, message);
    }

    // 파일에서 Message 객체를 역직렬화하여 읽어옴
    @Override
    public Message findById(UUID id) {
        Path filePath = getMessageFile(id);
        return readFile(filePath, Message.class);
    }

    public List<Message> findAll() {
        File[] files = MESSAGE_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        if (files == null) {
            return new ArrayList<>();
        }

        List<Message> result = new ArrayList<>();
        for (File f : files) {
            Message message = readFile(f.toPath(), Message.class);
            if (message != null) {
                result.add(message);
            }
        }

        // createdAt 기준 최신순으로 정렬.
        result.sort(Comparator.comparing(Message::getCreatedAt).reversed());

        return result;
    }

    @Override
    public void update(UUID id, String newMessageName) {
        Message message = findById(id);
        if (message == null) {
            throw new NoSuchElementException("No message file found for ID: " + id);
        }
        message.updateMessage(newMessageName);
        upsert(message);
    }

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
