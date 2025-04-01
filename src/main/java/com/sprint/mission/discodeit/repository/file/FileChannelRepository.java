package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.FileRepository;
import org.springframework.beans.factory.annotation.Value;
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

import static com.sprint.mission.discodeit.common.CodeitConstants.FILE_EXTENSION;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileChannelRepository implements ChannelRepository, FileRepository {
    private final Path CHANNEL_DIR;

    public FileChannelRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.CHANNEL_DIR = Paths.get(fileDirectory,"channeldata");
        createDirectories(CHANNEL_DIR);
    }

    private Path getChannelFile(UUID id) {
        return CHANNEL_DIR.resolve(id.toString() + FILE_EXTENSION);
    }

    // 파일 저장을 위한 경로
    @Override
    public void createDirectories(Path path)  {
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
    public void writeFile(Path path, Object obj)  {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

    // 파일 읽어오기
    @Override
    public <T> T readFile(Path path, Class<T> clazz)  {
        if (Files.exists(path) == false) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    @Override
    public void upsert(Channel channel) {
        Path filePath = getChannelFile(channel.getId());
        writeFile(filePath, channel);
    }

    @Override
    public Channel findById(UUID id) {
        Path filePath = getChannelFile(id);
        return readFile(filePath, Channel.class);
    }

    @Override
    public List<Channel> findAll() {
        File[] files = CHANNEL_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        if (files == null) {
            return new ArrayList<>();
        }

        List<Channel> result = new ArrayList<>();
        for (File f : files) {
            result.add(readFile(f.toPath(), Channel.class));
        }
        return result;
    }

    @Override
    public void update(UUID id, ChannelType type, String newChannelName, String description) {
        Channel channel = findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        channel.updateChannel(type, newChannelName, description);
        upsert(channel);
    }

    @Override
    public void delete(UUID id) {
        Path filePath = getChannelFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete channel file"+filePath, e);
        }
    }
}
