package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.FileRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserRepository implements UserRepository, FileRepository {
    private static final Path USER_DIR = Paths.get("output/userdata");

    public FileUserRepository() {
        try {
            createDirectories(USER_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory", e);
        }
    }

    private Path getUserFile(UUID id) {
        return USER_DIR.resolve(id.toString() + ".dat");
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
    public void upsert(User user) {
        Path filePath = getUserFile(user.getId());
        try {
            writeFile(filePath, user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upsert user", e);
        }
    }

    @Override
    public User findById(UUID id) {
        Path filePath = getUserFile(id);
        try {
            return readFile(filePath, User.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find user", e);
        }
    }

    @Override
    public List<User> findAll() {
        File[] files = USER_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<User> result = new ArrayList<>();
        for (File f : files) {
            try {
                result.add(readFile(f.toPath(), User.class));
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("Failed to find all user", e);
            }
        }
        return result;
    }

    @Override
    public void update(UUID id, String newUserName, String newEmail) {
        User user = findById(id);
        if (user == null) {
            throw new NoSuchElementException("No user file found for ID: " + id);
        }
        user.updateUser(newUserName, newEmail);
        upsert(user);
    }

    @Override
    public void delete(UUID id) {
        Path filePath = getUserFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete user file", e);
        }
    }
}