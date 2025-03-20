package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserLoginRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    public BasicAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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