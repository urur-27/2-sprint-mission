package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    @Override
    public UserResponse login(UserLoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equals(request.username())
                        && u.getPassword().equals(request.password()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), true);
    }
}