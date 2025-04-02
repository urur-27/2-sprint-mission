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
import java.util.*;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.common.CodeitConstants.FILE_EXTENSION;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "file", matchIfMissing = true)
public class FileBinaryContentRepository implements BinaryContentRepository, FileRepository {
    private final Path CONTENT_DIR;

    public FileBinaryContentRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.CONTENT_DIR = Paths.get(fileDirectory, "binarycontentdata");
        createDirectories(CONTENT_DIR);
    }

    private Path getFile(UUID id) {
        return CONTENT_DIR.resolve(id + FILE_EXTENSION);
    }

    @Override
    public void createDirectories(Path path) {
        try {
            if (Files.exists(path) == false) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        if (Files.exists(path) == false) return null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return clazz.cast(ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read file: " + path, e);
        }
    }

    @Override
    public UUID upsert(BinaryContent binaryContent) {
        Path filePath = getFile(binaryContent.getId());
        writeFile(filePath, binaryContent);
        return binaryContent.getId();
    }

    @Override
    public List<BinaryContent> findAll() {
        File[] files = CONTENT_DIR.toFile().listFiles((dir, name) -> name.endsWith(FILE_EXTENSION));
        List<BinaryContent> results = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                BinaryContent content = readFile(file.toPath(), BinaryContent.class);
                if (content != null) results.add(content);
            }
        }
        return results;
    }

    @Override
    public BinaryContent findById(UUID id) {
        Path filePath = getFile(id);
        return readFile(filePath, BinaryContent.class);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        Set<UUID> idSet = new HashSet<>(ids); // 빠른 조회를 위한 Set 변환
        return findAll().stream()
                .filter(content -> idSet.contains(content.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        Path filePath = getFile(id);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete binary content"+ filePath, e);
        }
    }
}

