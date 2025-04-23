package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileMapper {

  public BinaryContent toEntity(MultipartFile file) {
    return new BinaryContent(
        file.getOriginalFilename(),
        file.getSize(),
        file.getContentType()
    );
  }
}
