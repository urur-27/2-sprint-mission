package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  public MessageResponse toResponse(Message message) {
    // 작성자 정보 구성
    User user = message.getAuthor();
    UserResponse author = userMapper.toResponse(user);

    // 첨부 파일 변환
    List<BinaryContentResponse> attachments = message.getAttachments().stream()
        .filter(Objects::nonNull)
        .map(binaryContentMapper::toResponse)
        .toList();

    return new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannel().getId(),
        author,
        attachments
    );
  }
}

