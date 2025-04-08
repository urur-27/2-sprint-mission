package com.sprint.mission.discodeit.dto2.request;

import java.util.UUID;

public record UserUpdateRequest(
    String newUsername,
    String newEmail,
    String newPassword// 선택적 필드
) {

}