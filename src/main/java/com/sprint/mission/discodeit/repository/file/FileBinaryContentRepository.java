package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository, FileRepository {
    private final Path CONTENT_DIR;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.CONTENT_DIR = Paths.get(fileDirectory, "binarycontentdata");
        try {
            createDirectories(CONTENT_DIR);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create directory", e);
        }
    }

    private Path getFile(UUID id) {
        return CONTENT_DIR.resolve(id + ".dat");
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
        if (Files.exists(path) == false) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return clazz.cast(ois.readObject());
        }
    }

    @Override
    public UUID upsert(BinaryContent binaryContent) {
        Path filePath = getFile(binaryContent.getId());
        try {
            writeFile(filePath, binaryContent);
            return binaryContent.getId();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upsert binary content", e);
        }
    }

    @Override
    public List<BinaryContent> findAll() {
        File[] files = CONTENT_DIR.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
        List<BinaryContent> results = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                try {
                    BinaryContent content = readFile(file.toPath(), BinaryContent.class);
                    if (content != null) results.add(content);
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("Failed to load binary contents", e);
                }
            }
        }
        return results;
    }

    @Override
    public BinaryContent findById(UUID id) {
        Path filePath = getFile(id);
        try {
            return readFile(filePath, BinaryContent.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to find binary content", e);
        }
    }

    @Override
    public List<BinaryContent> findAllByIdIn(UUID messageId) {
        return findAll().stream()
                .filter(content -> content.getId().equals(messageId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByMessageId(UUID messageId) {
        findAllByIdIn(messageId).forEach(content -> delete(content.getId()));
    }

    @Override
    public void delete(UUID id) {
        Path filePath = getFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete binary content", e);
        }
    }
}

