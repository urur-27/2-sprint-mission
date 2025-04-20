package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

  //CRUD 기능을 선언
  User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);  // User 생성 (선택적 프로필 이미지 포함)

  User findById(UUID id);  // User 조회 (패스워드 제외 & 온라인 상태 포함)

  List<User> findAll();  // 모든 User 조회 (패스워드 제외 & 온라인 상태 포함)

  User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> profileCreateRequest);  // User 정보 수정 (선택적 프로필 이미지 변경)

  void delete(UUID id);  // User 삭제 (BinaryContent, UserStatus 같이 삭제)
}