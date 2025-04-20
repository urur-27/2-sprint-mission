package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  // 로컬 디스크 루트 경로
  private final Path root;

  public LocalBinaryContentStorage(
      // 설정값 주입
      @Value("${discodeit.storage.local.root-path}") String rootPath
  ) {
    this.root = Paths.get(rootPath);
  }

  // 루트 디렉토리 초기화. PostConstruct를 이용해 Bean 생성 후 자동 호출
  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new RestException(ResultCode.FILE_INITIALIZATION_ERROR);
    }
  }

  @Override
  public UUID put(UUID uuid, byte[] bytes) {
    Path path = resolvePath(uuid);
    try {
      Files.write(path, bytes);
      return uuid;
    } catch (IOException e) {
      throw new RestException(ResultCode.FILE_WRITE_ERROR);
    }
  }

  @Override
  public InputStream get(UUID uuid) {
    Path path = resolvePath(uuid);
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentResponse binaryContentResponse) {
    Path path = resolvePath(binaryContentResponse.id());
    try {
      byte[] bytes = Files.readAllBytes(path);
      ByteArrayResource resource = new ByteArrayResource(bytes);

      return ResponseEntity.ok()
          .contentLength(binaryContentResponse.size())
          .contentType(MediaType.parseMediaType(binaryContentResponse.contentType()))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\"" + binaryContentResponse.fileName() + "\"")
          .body(resource);
    } catch (IOException e) {
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }
  }

  @Override
  public void delete(UUID uuid) {
    Path path = resolvePath(uuid);
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }
  }

  // 파일의 실제 저장 위치에 대한 규칙 정의
  private Path resolvePath(UUID uuid) {
    return root.resolve(uuid.toString());
  }
}
