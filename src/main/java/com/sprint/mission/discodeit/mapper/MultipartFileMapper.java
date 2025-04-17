package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileMapper {

  public BinaryContent toEntity(MultipartFile file) {
    try {
      return new BinaryContent(
          file.getOriginalFilename(),
          file.getSize(),
          file.getContentType(),
          file.getBytes()
      );
    } catch (IOException e) {
      throw new RuntimeException("파일 변환 실패", e);
    }
  }
}
