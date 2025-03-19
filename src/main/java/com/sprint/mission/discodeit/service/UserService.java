package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface UserService {
    //CRUD 기능을 선언
    UUID create(UserCreateRequest request);  // User 생성 (선택적 프로필 이미지 포함)
    UserResponse findById(UUID id);  // User 조회 (패스워드 제외 & 온라인 상태 포함)
    List<UserResponse> findAll();  // 모든 User 조회 (패스워드 제외 & 온라인 상태 포함)
    void update(UserUpdateRequest request);  // User 정보 수정 (선택적 프로필 이미지 변경)
    void delete(UUID id);  // User 삭제 (BinaryContent, UserStatus 같이 삭제)

}