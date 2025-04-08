package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
//    @NotBlank(message = "아이디는 필수입니다.")
    String username,
//    @NotBlank(message = "이메일은 필수입니다.")
//    @Email(message = "이메일 형식이 올바르지 않습니다.")
    String email,
//    @NotBlank(message = "비밀번호는 필수입니다.")
    String password
) {

}
