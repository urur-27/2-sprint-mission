package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final BinaryContentMapper binaryContentMapper;

  public UserResponse toResponse(User user, boolean isOnline) {
    BinaryContentResponse profile = user.getProfile() != null
        ? binaryContentMapper.toResponse(user.getProfile())
        : null;

    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profile,
        isOnline,
        user.getRole()
    );
  }
}