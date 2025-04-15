package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Component;

@Component
public class BinaryContentMapper {

  public BinaryContentResponse toResponse(BinaryContent content) {
    return new BinaryContentResponse(
        content.getId(),
        content.getFileName(),
        content.getSize(),
        content.getContentType()
    );
  }
}
