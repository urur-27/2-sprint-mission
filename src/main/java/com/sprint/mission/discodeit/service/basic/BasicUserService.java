package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
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
    private static volatile BasicUserService instance;
    private final UserRepository userRepository;
    BinaryContentRepository BinaryContentRepository;
    UserStatusRepository UserStatusRepository;

    @Override
    public UUID create(UserCreateRequest request) {
        // username과 email의 중복 검사
        // 모든 데이터를 찾아서 그곳에 username, 혹은 email 중복이 있는지 체크
        for (User user : userRepository.findAll()) {
            if (user.getUsername().equals(request.username())) {
                throw new IllegalArgumentException("이미 존재하는 username입니다.");
            }
            if (user.getEmail().equals(request.email())) {
                throw new IllegalArgumentException("이미 존재하는 email입니다.");
            }
        }

        // User 생성
        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );
        userRepository.upsert(user);

        saveProfileImage(user.getId(), request.profileImage());
        UserStatusRepository.save(request.status());

        return user.getId();
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저를 찾지 못했습니다. : " + id);
        }
        boolean isOnline = UserStatusRepository.isUserOnline(id);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), isOnline);
    }


    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), UserStatusRepository.isUserOnline(user.getId())))
                .toList();
    }

    @Override
    public void update(UserUpdateRequest request) {
        // 기존 유저 정보 조회
        User user = userRepository.findById(request.userId());
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저를 찾을 수 없습니다: " + request.userId());
        }

        // 사용자 정보 업데이트 (이름 & 이메일)
        userRepository.update(request.userId(), request.username(), request.email(), request.password());

        saveProfileImage(request.userId(), request.profileImage());
    }

    @Override
    public void delete(UUID id) {
        // 사용자 존재 여부 확인
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("해당 ID의 유저를 찾을 수 없습니다: " + id);
        }

        // 관련 데이터 삭제 (프로필 이미지, 유저 상태)
        BinaryContentRepository.deleteProfileImageByUserId(id);
        UserStatusRepository.deleteByUserId(id);
        // 최종적으로 사용자 삭제
        userRepository.delete(id);
    }

    private void saveProfileImage(UUID userId, byte[] profileImage) {
        if (profileImage != null) {
            BinaryContentRepository.save(new BinaryContent(userId, null, profileImage, "image/png", profileImage.length));
        }
    }

}
