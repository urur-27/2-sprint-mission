package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.data.UserDto;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
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
  private final BinaryContentRepository BinaryContentRepository;
  private final UserStatusRepository UserStatusRepository;

  @Override
  public UUID create(UserCreateRequest request) {
    // username과 email의 중복 검사
    // 모든 데이터를 찾아서 그곳에 username, 혹은 email 중복이 있는지 체크
    for (User user : userRepository.findAll()) {
      if (user.getUsername().equals(request.username())) {
        throw new IllegalArgumentException("The username already exists.");
      }
      if (user.getEmail().equals(request.email())) {
        throw new IllegalArgumentException("This email already exists.");
      }
    }

    UUID profileImage = saveProfileImage(request.profileImage());

    // User 생성
    User user = new User(
        request.username(),
        request.email(),
        request.password(),
        profileImage
    );
    userRepository.upsert(user);

    UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
    UserStatusRepository.upsert(userStatus);

    return user.getId();
  }

  @Override
  public UserResponse findById(UUID id) {
    User user = userRepository.findById(id);
    if (user == null) {
      throw new NoSuchElementException("Could not find user with that ID. : " + id);
    }
    boolean isOnline = UserStatusRepository.isUserOnline(id);
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), isOnline);
  }


  @Override
  public List<UserDto> findAll() {
    return userRepository.findAll().stream()
        .map(user -> new UserDto(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
            user.getUsername(), user.getEmail(), user.getProfileId(),
            UserStatusRepository.isUserOnline(user.getId())))
        .toList();
  }

  @Override
  public void update(UserUpdateRequest request) {
    // 기존 유저 정보 조회
    User user = userRepository.findById(request.userId());
    if (user == null) {
      throw new NoSuchElementException("Could not find user with that ID. : " + request.userId());
    }

    if (request.profileImage() != null) {
      UUID profileId = saveProfileImage(request.profileImage());
      // 사용자 정보 업데이트 (이름 & 이메일)
      userRepository.update(request.userId(), request.username(), request.email(),
          request.password(), profileId);
    }
    userRepository.update(request.userId(), request.username(), request.email(), request.password(),
        null);
  }

  @Override
  public void delete(UUID id) {
    // 사용자 존재 여부 확인
    User user = userRepository.findById(id);
    if (user == null) {
      throw new NoSuchElementException("Could not find user with that ID. : " + id);
    }

    // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
    BinaryContentRepository.delete(user.getProfileId());
    UserStatusRepository.deleteByUserId(id);
    // 최종적으로 사용자 삭제
    userRepository.delete(id);
  }

  private UUID saveProfileImage(byte[] profileImage) {
    if (profileImage == null || profileImage.length == 0) {
      return null;
//            throw new IllegalArgumentException("Profile image cannot be null or empty");
    }
    return BinaryContentRepository.upsert(
        new BinaryContent(profileImage, "image/png", profileImage.length));
  }
}
