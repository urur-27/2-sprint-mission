package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.SessionInvalidateManager;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final SessionInvalidateManager sessionInvalidateManager;
    private UserMapper userMapper;

    @Transactional
    @Override
    public UserResponse updateUserRole(RoleUpdateRequest request){
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND));

        user.setRole(request.newRole());

        userRepository.save(user);

        // 세션 무효화
        sessionInvalidateManager.invalidateIfPresent(user.getId());

        return userMapper.toResponse(user, false);
    }
}
