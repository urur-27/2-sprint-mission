package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;

  public UserResponse toResponse(User user) {
    BinaryContentResponse profile = null;

    if (user.getProfile() != null && user.getProfile().getId() != null) {
      profile = binaryContentRepository.findById(user.getProfile().getId())
          .map(content -> new BinaryContentResponse(
              content.getId(),
              content.getFileName(),
              content.getSize(),
              content.getContentType()
          ))
          .orElse(null);
    }

    boolean isOnline = userStatusRepository.isUserOnline(user.getId(), Instant.now());

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