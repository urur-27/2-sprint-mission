package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.CodeitConstants;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.notfound.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
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
  private final BinaryContentMapper binaryContentMapper;

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

    User newUser = userRepository.save(new User(
        userCreateRequest.username(),
        userCreateRequest.email(),
        userCreateRequest.password(),
        newProfile
    ));

    userStatusRepository.save(new UserStatus(newUser, Instant.now()));

    return newUser;
  }

  @Override
  @Transactional(readOnly = true)
  public User findById(UUID id) {

//    boolean isOnline = userStatusRepository.isUserOnline(user.getId(), Instant.now()
//        .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));
//
//    BinaryContentResponse profile = Optional.ofNullable(user.getProfile())
//        .map(binaryContentMapper::toResponse).orElse(null);

    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
  }


  @Override
  @Transactional(readOnly = true)
  public List<User> findAll() {
    return userRepository.findAll();
//    .stream()
//        .map(user -> {
//          BinaryContentResponse profile = Optional.ofNullable(user.getProfile())
//              .map(binaryContentMapper::toResponse)
//              .orElse(null);
//
//          boolean isOnline = userStatusRepository.isUserOnline(user.getId(), Instant.now()
//              .minusSeconds(CodeitConstants.ONLINE_THRESHOLD_SECONDS));
//
//          return user;
//          );
//        })
//        .toList();
  }

  @Override
  @Transactional
  public User update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException(userId));

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
        .orElseThrow(() -> new UserNotFoundException(id));

    // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
    Optional.ofNullable(user.getProfile())
        .ifPresent(binaryContentRepository::delete);
    // 최종적으로 사용자 삭제
    userRepository.delete(user);
  }

  // email과 username 중복 체크
  private void validateDuplicate(String username, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new DuplicateUsernameException(username);
    }
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException(email);
    }
  }

  // BianryContent 생성 로직
  private BinaryContent saveBinaryContent(BinaryContentCreateRequest request) {
    BinaryContent content = new BinaryContent(
        request.fileName(),
        (long) request.bytes().length,
        request.contentType(),
        request.bytes()
    );
    return binaryContentRepository.save(content);
  }

}
