package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  public UserResponse toResponse(User user) {
    BinaryContentResponse profile = null;

    if (user.getProfile().getId() != null) {
      BinaryContent content = binaryContentRepository.findById(user.getProfile().getId());
      if (content != null) {
        profile = new BinaryContentResponse(
            content.getId(),
            content.getFileName(),
            content.getSize(),
            content.getContentType()
        );
      }
    }

    boolean isOnline = userStatusRepository.isUserOnline(user.getId());

    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profile,
        isOnline
    );
  }
}