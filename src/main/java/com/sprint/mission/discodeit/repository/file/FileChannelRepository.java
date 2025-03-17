package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.FileRepository;
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
public class FileChannelRepository implements ChannelRepository, FileRepository {
    private static final Path CHANNEL_DIR = Paths.get("output/channeldata");

    public FileChannelRepository() {
        try {
            createDirectories(CHANNEL_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory", e);
        }
    }

    private Path getChannelFile(UUID id) {
        return CHANNEL_DIR.resolve(id.toString() + ".dat");
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

    @Override
    public void upsert(Channel channel) {
        Path filePath = getChannelFile(channel.getId());
        try {
            writeFile(filePath, channel);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upsert channel", e);
        }
    }

    @Override
    public Channel findById(UUID id) {
        Path filePath = getChannelFile(id);
        try {
            return readFile(filePath, Channel.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find channel", e);
        }
    }

    @Override
    public List<Channel> findAll() {
        File[] files = CHANNEL_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<Channel> result = new ArrayList<>();
        for (File f : files) {
            try {
                result.add(readFile(f.toPath(), Channel.class));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to find all channel", e);
            }
        }
        return result;
    }

    @Override
    public void update(UUID id, String newChannelName) {
        Channel channel = findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        channel.updateChannel(newChannelName);
        upsert(channel);
    }

    @Override
    public void delete(UUID id) {
        Path filePath = getChannelFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete channel file", e);
        }
    }
}
