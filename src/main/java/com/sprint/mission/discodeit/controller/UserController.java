package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;
import com.sprint.mission.discodeit.util.LogUtils;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final BasicBinaryContentService binaryContentService;

  // User 등록
  @PostMapping(consumes = "multipart/form-data")
  public ResponseEntity<UserResponse> createUser(
      @Valid @RequestPart("userCreateRequest") String userCreateRequestJson,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[CREATE] status=START, userCreateRequest={}, traceId={}",
        log.isDebugEnabled() ? userCreateRequestJson : "***", traceId);

    UserCreateRequest userCreateRequest;
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      userCreateRequest = objectMapper.readValue(userCreateRequestJson, UserCreateRequest.class);
    } catch (IOException e) {
      log.warn("[CREATE] Invalid JSON format for User creation: traceId={}", traceId, e);
      throw new RestException(ResultCode.INVALID_JSON);
    }

    Optional<BinaryContentCreateRequest> profileRequest =
        binaryContentService.resolveProfileRequest(profile);

    User createdUser = userService.create(userCreateRequest, profileRequest);
    UserResponse response = userService.getUserResponse(createdUser);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(createdUser.getId()), traceId);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(response);
  }

  // User 수정
  @PatchMapping(value = "/{userId}", consumes = "multipart/form-data")
  @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID userId,
      @Valid @RequestPart("userUpdateRequest") UserUpdateRequest userUpdateRequest,
      @RequestPart(value = "profile", required = false) MultipartFile profile) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    Optional<BinaryContentCreateRequest> profileRequest =
        binaryContentService.resolveProfileRequest(profile);
    User updatedUser = userService.update(userId, userUpdateRequest, profileRequest);
    UserResponse response = userService.getUserResponse(updatedUser);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(userId), traceId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(response);
  }

  // User 삭제
  @DeleteMapping("/{userId}")
  @PreAuthorize("#userId == authentication.principal.id or hasRole('ROLE_ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    userService.delete(userId);

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(userId), traceId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }

  // 모든 유저 조회
  @GetMapping
  public ResponseEntity<List<UserResponse>> getUsers() {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, traceId={}", traceId);

    List<User> users = userService.findAll();
    List<UserResponse> responses = users.stream()
        .map(userService::getUserResponse)
        .toList();

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userCount={}, traceId={}",
        responses.size(), traceId);

    return ResponseEntity.ok(responses);
  }

}
