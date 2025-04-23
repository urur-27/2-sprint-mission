package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.common.CodeitConstants;
import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final UserStatusRepository userStatusRepository;
  private final BinaryContentMapper binaryContentMapper;

  public UserResponse toResponse(User user) {
    // profile은 nullable
    BinaryContentResponse profile = null;

    if (user.getProfile() != null) {
      profile = binaryContentMapper.toResponse(user.getProfile());
    }

    boolean isOnline = userStatusRepository.isUserOnline(user.getId(), Instant.now()
        .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));

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