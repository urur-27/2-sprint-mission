package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionInvalidateManager;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final SessionInvalidateManager sessionInvalidateManager;
    private UserMapper userMapper;

    @Transactional
    @Override
    public UserDto updateUserRole(RoleUpdateRequest request){
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new DiscodeitException(ErrorCode.USER_NOT_FOUND));

        user.setRole(request.newRole());

        userRepository.save(user);

        // 세션 무효화
        sessionInvalidateManager.invalidateIfPresent(user.getId());

        return userMapper.toDto(user);
    }
}
