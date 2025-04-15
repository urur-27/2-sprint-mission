package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageMapper {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentRepository binaryContentRepository;

  public MessageResponse toResponse(Message message) {
    // 작성자 정보 구성
    User user = userRepository.findById(message.getAuthorId());
    BinaryContent profile = binaryContentRepository.findById(user.getProfileId());
    BinaryContentResponse profileResponse = profile != null ? new BinaryContentResponse(
        profile.getId(), profile.getFileName(), profile.getSize(), profile.getContentType()
    ) : null;

    boolean isOnline = userStatusRepository.isUserOnline(user.getId());
    UserResponse author = new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profileResponse,
        isOnline
    );

    // 첨부 파일 변환
    List<BinaryContentResponse> attachments = message.getAttachmentIds().stream()
        .map(binaryContentRepository::findById)
        .filter(Objects::nonNull)
        .map(content -> new BinaryContentResponse(
            content.getId(), content.getFileName(), content.getSize(), content.getContentType()
        ))
        .toList();

    return new MessageResponse(
        message.getId(),
        message.getCreatedAt(),
        message.getUpdatedAt(),
        message.getContent(),
        message.getChannelId(),
        author,
        attachments
    );
  }
}

