package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto2.response.UserStatusResponse;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStatusMapper {

  public UserStatusResponse toResponse(UserStatus userStatus) {
    return new UserStatusResponse(
        userStatus.getId(),
        userStatus.getUser().getId(),
        userStatus.getLastActiveAt());
  }
}
