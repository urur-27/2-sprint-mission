package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileUserStatusRepository implements UserStatusRepository, FileRepository {
    private static final Path USERSTATUS_DIR = Paths.get("output/userstatusdata");

    public FileUserStatusRepository() {
        try {
            createDirectories(USERSTATUS_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory", e);
        }
    }

    private Path getFile(UUID userId) {
        return USERSTATUS_DIR.resolve(userId + ".dat");
    }

    @Override
    public void createDirectories(Path path) throws IOException {
        if (Files.exists(path) == false) {
            Files.createDirectories(path);
        }
    }

    @Override
    public void writeFile(Path path, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        }
    }

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
    public void upsert(UserStatus userStatus) {
        Path filePath = getFile(userStatus.getUserId());
        try {
            writeFile(filePath, userStatus);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upsert user status", e);
        }
    }

    @Override
    public boolean isUserOnline(UUID userId) {
        UserStatus status = findByUserId(userId);
        return status != null && status.isCurrentOnline();
    }

    @Override
    public List<UserStatus> findAllOnlineUsers() {
        return findAll().stream()
                .filter(UserStatus::isCurrentOnline)
                .collect(Collectors.toList());
    }

    @Override
    public void updateLastAccessedAt(UUID userId, Instant lastAccessedAt) {
        UserStatus status = findByUserId(userId);
        if (status != null) {
            status.updateLastAccessedAt(lastAccessedAt);
            upsert(status);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        Path filePath = getFile(userId);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user status", e);
        }
    }

    private UserStatus findByUserId(UUID userId) {
        Path filePath = getFile(userId);
        try {
            return readFile(filePath, UserStatus.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find user status", e);
        }
    }

    private List<UserStatus> findAll() {
        File[] files = USERSTATUS_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        List<UserStatus> results = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                try {
                    UserStatus status = readFile(file.toPath(), UserStatus.class);
                    if (status != null) results.add(status);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("Failed to load user statuses", e);
                }
            }
        }
        return results;
    }
}
