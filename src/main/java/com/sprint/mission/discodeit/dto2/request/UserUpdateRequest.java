package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(max = 50, message = "New username must be at most 50 characters.")
    String newUsername,

    @Email(message = "Invalid email format.")
    @Size(max = 100, message = "New email must be at most 100 characters.")
    String newEmail,

    @Size(min = 8, max = 60, message = "New password must be between 8 and 60 characters.")
    String newPassword// 선택적 필드
) {

}