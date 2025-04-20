package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BinaryContentMapper {

  private final BinaryContentStorage binaryContentStorage;

  public BinaryContentResponse toResponse(BinaryContent content) {
    if (content == null) {
      return null; // BianryContent가 없으면 null 반환
    }

    if (content.getId() == null) {
      throw new IllegalStateException("BinaryContent must be saved before mapping to DTO.");
    }
    
    try {
      byte[] bytes = binaryContentStorage.get(content.getId()).readAllBytes();
      return new BinaryContentResponse(
          content.getId(),
          content.getFileName(),
          content.getSize(),
          content.getContentType(),
          bytes
      );
    } catch (IOException e) {
      throw new RuntimeException("Failed to read binary content", e);
    }
  }
}
