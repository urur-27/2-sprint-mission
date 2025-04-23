package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusMapper {

  public ReadStatusResponse toResponse(ReadStatus readStatus) {
    return new ReadStatusResponse(
        readStatus.getId(),
        readStatus.getUser().getId(),
        readStatus.getChannel().getId(),
        readStatus.getLastReadAt()
    );
  }

}
