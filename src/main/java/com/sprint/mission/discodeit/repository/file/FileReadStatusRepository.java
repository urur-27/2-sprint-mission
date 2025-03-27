package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileReadStatusRepository implements ReadStatusRepository, FileRepository {
    private final Path READSTATUS_DIR;

    public FileReadStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.READSTATUS_DIR = Paths.get(fileDirectory, "readstatusdata");
        createDirectories(READSTATUS_DIR);
    }

    private Path getFile(UUID id) {
        return READSTATUS_DIR.resolve(id.toString() + ".dat");
    }

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

    @Override
    public void writeFile(Path path, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + path, e);
        }
    }

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

    @Override
    public void upsert(ReadStatus readStatus) {
        Path filePath = getFile(readStatus.getId());
        writeFile(filePath, readStatus);
    }

    @Override
    public List<UUID> findUsersByChannelId(UUID channelId) {
        List<UUID> users = new ArrayList<>();
        findAll().forEach(rs -> {
            if (rs.getChannelId().equals(channelId)) {
                users.add(rs.getUserId());
            }
        });
        return users;
    }

    @Override
    public ReadStatus findById(UUID readStatusId) {
        Path filePath = getFile(readStatusId);
        return readFile(filePath, ReadStatus.class);
    }

    @Override
    public List<ReadStatus> findAllByUser(UUID userId) {
        List<ReadStatus> result = new ArrayList<>();
        findAll().forEach(rs -> {
            if (rs.getUserId().equals(userId)) {
                result.add(rs);
            }
        });
        return result;
    }

    private List<ReadStatus> findAll() {
        File[] files = READSTATUS_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        List<ReadStatus> results = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                ReadStatus readStatus = readFile(file.toPath(), ReadStatus.class); // 내부에서 예외 처리됨
                if (readStatus != null) {
                    results.add(readStatus);
                }
            }
        }
        return results;
    }

    @Override
    public void updateLastReadAt(UUID userId, UUID channelId, Instant lastReadAt) {
        findAllByUser(userId).forEach(rs -> {
            if (rs.getChannelId().equals(channelId)) {
                rs.updateReadStatus(lastReadAt);
                upsert(rs);
            }
        });
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        findAll().forEach(rs -> {
            if (rs.getChannelId().equals(channelId)) delete(rs.getId());
        });
    }

    @Override
    public void delete(UUID readStatusId) {
        Path filePath = getFile(readStatusId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete read status" + filePath, e);
        }
    }
}
