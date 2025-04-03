package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
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
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.common.CodeitConstants.FILE_EXTENSION;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileUserStatusRepository implements UserStatusRepository, FileRepository {

  private final Path USERSTATUS_DIR;

  public FileUserStatusRepository(
      @Value("${discodeit.repository.file-directory}") String fileDirectory) {
    this.USERSTATUS_DIR = Paths.get(fileDirectory, "userstatusdata");
    createDirectories(USERSTATUS_DIR);
  }

  private Path getFile(UUID userId) {
    return USERSTATUS_DIR.resolve(userId + FILE_EXTENSION);
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
  public UserStatus upsert(UserStatus userStatus) {
    Path filePath = getFile(userStatus.getUserId());
    writeFile(filePath, userStatus);
    return userStatus;
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

  @Override
  public UserStatus findById(UUID id) {
    UserStatus userStatusNullable = null;
    Path path = getFile(id);
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        userStatusNullable = (UserStatus) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return userStatusNullable;
  }

  @Override
  public UserStatus findByUserId(UUID userId) {
    Path filePath = getFile(userId);
    return readFile(filePath, UserStatus.class);
  }

  private List<UserStatus> findAll() {
    File[] files = USERSTATUS_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
    List<UserStatus> results = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
        UserStatus status = readFile(file.toPath(), UserStatus.class);
        if (status != null) {
          results.add(status);
        }
      }
    }
    return results;
  }
}
