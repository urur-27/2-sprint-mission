package com.sprint.mission.discodeit.service.basic;

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
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final UserStatusRepository userStatusRepository;
  private final BinaryContentMapper binaryContentMapper;
  private final UserMapper userMapper;

  @Override
  public UserResponse create(UserCreateRequest userCreateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    String username = userCreateRequest.username();
    String email = userCreateRequest.email();

    // 예외처리
    if (userRepository.existsByEmail(email)) {
      throw new DuplicateEmailException(email);
    }
    if (userRepository.existsByUsername(username)) {
      throw new DuplicateUsernameException(username);
    }

    // ProfileId 생성, UserService가 아니라 다른 곳에서 처리 고려하기
    UUID nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType, bytes);
          return binaryContentRepository.upsert(binaryContent).getId();
        })
        .orElse(null);
    String password = userCreateRequest.password();

    User user = new User(username, email, password, nullableProfileId);
    User createdUser = userRepository.upsert(user);

    Instant now = Instant.now();
    UserStatus userStatus = new UserStatus(createdUser.getId(), now);
    userStatusRepository.upsert(userStatus);

    return userMapper.toResponse(createdUser);
  }

  @Override
  public UserResponse findById(UUID id) {
    User user = userRepository.findById(id);
    if (user == null) {
      throw new NoSuchElementException("Could not find user with that ID. : " + id);
    }
    boolean isOnline = userStatusRepository.isUserOnline(id);

    BinaryContent content = binaryContentRepository.findById(user.getProfileId());
    BinaryContentResponse profile =
        content != null ? binaryContentMapper.toResponse(content) : null;

    return new UserResponse(
        user.getId(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUsername(),
        user.getEmail(),
        profile,
        isOnline
    );
  }


  @Override
  public List<UserResponse> findAll() {
    return userRepository.findAll().stream()
        .map(user -> {
          UUID profileId = user.getProfileId();
          BinaryContentResponse profile = null;

          if (profileId != null) {
            BinaryContent content = binaryContentRepository.findById(profileId);
            profile = content != null ? binaryContentMapper.toResponse(content) : null;
          }

          boolean isOnline = userStatusRepository.isUserOnline(user.getId());

          return new UserResponse(
              user.getId(),
              user.getCreatedAt(),
              user.getUpdatedAt(),
              user.getUsername(),
              user.getEmail(),
              profile,
              isOnline
          );
        })
        .toList();
  }

  @Override
  public UserResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      Optional<BinaryContentCreateRequest> optionalProfileCreateRequest) {
    User user = userRepository.findById(userId);
    if (user == null) {
      throw new UserNotFoundException(userId);
    }

    String newUsername = userUpdateRequest.newUsername();
    String newEmail = userUpdateRequest.newEmail();
    if (userRepository.existsByEmail(newEmail)) {
      throw new DuplicateEmailException(newEmail);
    }
    if (userRepository.existsByUsername(newUsername)) {
      throw new DuplicateUsernameException(newUsername);
    }

    UUID nullableProfileId = optionalProfileCreateRequest
        .map(profileRequest -> {
          Optional.ofNullable(user.getProfileId())
              .ifPresent(binaryContentRepository::delete);

          String fileName = profileRequest.fileName();
          String contentType = profileRequest.contentType();
          byte[] bytes = profileRequest.bytes();
          BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length,
              contentType, bytes);
          return binaryContentRepository.upsert(binaryContent).getId();
        })
        .orElse(null);

    String newPassword = userUpdateRequest.newPassword();
    user.updateUser(newUsername, newEmail, newPassword, nullableProfileId);

    return userMapper.toResponse(userRepository.upsert(user));
  }

  @Override
  public void delete(UUID id) {
    // 사용자 존재 여부 확인
    User user = userRepository.findById(id);
    if (user == null) {
      throw new UserNotFoundException(id);
    }

    // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
    binaryContentRepository.delete(user.getProfileId());
    userStatusRepository.deleteByUserId(id);
    // 최종적으로 사용자 삭제
    userRepository.delete(id);
  }
}
