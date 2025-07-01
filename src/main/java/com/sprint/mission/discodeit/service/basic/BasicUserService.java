package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.CodeitConstants;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.LogUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[CREATE] status=START, username={}, email={}, traceId={}",
        log.isDebugEnabled() ? userCreateRequest.username()
            : LogUtils.mask(userCreateRequest.username()),
        log.isDebugEnabled() ? userCreateRequest.email()
            : LogUtils.maskEmail(userCreateRequest.email()),
        traceId);

    // 예외처리
    validateDuplicate(userCreateRequest.username(), userCreateRequest.email());

    // 프로필 저장
    BinaryContent newProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);

    // 패스워드 인코딩
    String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

    User newUser = User.builder()
        .username(userCreateRequest.username())
        .email(userCreateRequest.email())
        .password(encodedPassword)
        .profile(newProfile)
        .role(Role.ROLE_USER)
        .build();

    userRepository.save(newUser);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(newUser.getId()), traceId);
    return newUser;
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[FIND] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    // 성공 로그
    log.info("[FIND] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(user.getId()), traceId);
    return user;
  }


  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, traceId={}", traceId);

    List<User> users = userRepository.findAll();

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userCount={}, traceId={}",
        users.size(), traceId);
    return users;
  }

  @Override
  @Transactional
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, userId={}, newUsername={}, newEmail={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId),
        log.isDebugEnabled() ? userUpdateRequest.newUsername()
            : LogUtils.mask(userUpdateRequest.newUsername()),
        log.isDebugEnabled() ? userUpdateRequest.newEmail()
            : LogUtils.maskEmail(userUpdateRequest.newEmail()),
        traceId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[UPDATE] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    validateDuplicate(newUsername, newEmail);

    // 새로운 프로필이 있는 경우에만 이전 프로필 삭제
    if (optionalProfileCreateRequest.isPresent()) {
      Optional.ofNullable(user.getProfile())
          .ifPresent(binaryContentRepository::delete);
    }

    // 새 프로필 저장
    BinaryContent newProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.updateUser(newUsername, newEmail, newPassword, newProfile);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(userId), traceId);
    return user;
  }

  @Override
  @Transactional
  public void delete(UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    // 사용자 존재 여부 확인
    User user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("[DELETE] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(userId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
    Optional.ofNullable(user.getProfile())
        .ifPresent(binaryContentRepository::delete);

    // 최종적으로 사용자 삭제
    userRepository.delete(user);

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(userId), traceId);
  }

  // email과 username 중복 체크
  private void validateDuplicate(String username, String email) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[VALIDATE] status=START, username={}, email={}, traceId={}",
        log.isDebugEnabled() ? username : LogUtils.mask(username),
        log.isDebugEnabled() ? email : LogUtils.maskEmail(email),
        traceId);

    if (userRepository.existsByUsername(username)) {
      log.warn("[VALIDATE] Duplicate username detected: username={}, traceId={}",
          username, traceId);
      throw new RestException(ResultCode.DUPLICATE_USERNAME);
    }

    if (userRepository.existsByEmail(email)) {
      log.warn("[VALIDATE] Duplicate email detected: email={}, traceId={}",
          LogUtils.maskEmail(email), traceId);
      throw new RestException(ResultCode.DUPLICATE_EMAIL);
    }

    // 성공 로그
    log.info("[VALIDATE] status=SUCCESS, username={}, email={}, traceId={}",
        LogUtils.mask(username), LogUtils.maskEmail(email), traceId);
  }

  // BianryContent 생성 로직
  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[SAVE] status=START, fileName={}, traceId={}",
        log.isDebugEnabled() ? request.fileName() : LogUtils.maskFileName(request.fileName()),
        traceId);

    BinaryContent content = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );

    // 먼저 DB에 저장해서 ID를 생성
    BinaryContent savedContent = binaryContentRepository.save(content);

    try {
      // 파일 저장
      binaryContentStorage.put(savedContent.getId(), request.bytes());
      log.info("[SAVE] BinaryContent saved successfully: contentId={}, traceId={}",
          savedContent.getId(), traceId);
    } catch (Exception e) {
      log.error("[SAVE] Error saving binary content: contentId={}, traceId={}",
          savedContent.getId(), traceId, e);
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }

    return savedContent;
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserResponse(User user) {
    boolean isOnline = user.getLastActiveAt() != null &&
        user.getLastActiveAt()
            .isAfter(Instant.now().minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));

    // 성공 로그
    log.info("[RESPONSE] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(user.getId()), MDC.get("traceId"));
    return userMapper.toResponse(user, isOnline);
  }

}
