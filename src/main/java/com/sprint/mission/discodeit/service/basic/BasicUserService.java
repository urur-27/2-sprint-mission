package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public User create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    // 예외처리
    validateDuplicate(userCreateRequest.username(), userCreateRequest.email());

    // 프로필 저장
    BinaryContent newProfile = optionalProfileCreateRequest
        .map(this::saveBinaryContent)
        .orElse(null);

    User newUser = User.builder()
        .username(userCreateRequest.username())
        .email(userCreateRequest.email())
        .password(userCreateRequest.password())
        .profile(newProfile)
        .build();

    userStatusRepository.save(new UserStatus(newUser, Instant.now()));

    return newUser;
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(UUID id) {

    return userRepository.findById(id)
        .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND));
  }


  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  @Transactional
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND));

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

    return user;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    // 사용자 존재 여부 확인
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND));

    // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
    Optional.ofNullable(user.getProfile())
        .ifPresent(binaryContentRepository::delete);
    // 최종적으로 사용자 삭제
    userRepository.delete(user);
  }

  // email과 username 중복 체크
  private void validateDuplicate(String username, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new RestException(ResultCode.DUPLICATE_USERNAME);
    }
    if (userRepository.existsByEmail(email)) {
      throw new RestException(ResultCode.DUPLICATE_EMAIL);
    }
  }

  // BianryContent 생성 로직
  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    BinaryContent content = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType()
    );

    // 먼저 DB에 저장해서 ID를 생성
    BinaryContent savedContent = binaryContentRepository.save(content);

    // ID가 생긴 후에야 파일 저장 가능
    try {
      binaryContentStorage.put(savedContent.getId(), request.bytes());
    } catch (Exception e) {
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }

    return savedContent;
  }

}
