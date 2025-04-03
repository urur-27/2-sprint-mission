package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.data.UserDto;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final UserStatusService userStatusService;
  private final AuthService authService;

  // User 등록
  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<User> createUser(
      @RequestPart("userCreateRequest") UserCreateRequest userCreateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User createdUser = userService.create(userCreateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(createdUser);
  }

  // User 수정
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  public ResponseEntity<User> updateUser(
      @PathVariable UUID userId,
      @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
        .flatMap(this::resolveProfileRequest);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUser);
  }

  // User 삭제
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    userService.delete(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }


  // 모든 유저 조회
  @GetMapping
  public ResponseEntity<List<UserDto>> getUsers() {
    List<UserDto> users = userService.findAll();
    return ResponseEntity.ok(users);
  }

  // 온라인 상태 갱신
  @PatchMapping("/{userId}/userStatus")
  public ResponseEntity<UserStatus> updateUserStatus(@PathVariable UUID userId,
      @RequestBody UserStatusUpdateRequest request) {
    UserStatus updatedUserStatus = userStatusService.updateByUserId(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(updatedUserStatus);
  }

//  @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
//  public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request) {
//    UserResponse response = authService.login(request);
//    return ResponseEntity.ok(response);
//  }


  private Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    if (profileFile.isEmpty()) {
      return Optional.empty();
    } else {
      try {
        BinaryContentCreateRequest binaryContentCreateRequest = new BinaryContentCreateRequest(
            profileFile.getOriginalFilename(),
            profileFile.getContentType(),
            profileFile.getBytes()
        );
        return Optional.of(binaryContentCreateRequest);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
