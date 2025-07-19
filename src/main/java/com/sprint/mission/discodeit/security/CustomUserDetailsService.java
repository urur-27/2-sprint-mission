package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userKey) throws UsernameNotFoundException {
        log.debug(">>>>> loadUserByUsername() called with userKey: {}", userKey);

        Optional<User> userOpt;

        // UUID로 변환 시도
        try {
            UUID userId = UUID.fromString(userKey);
            userOpt = userRepository.findById(userId);
            log.debug("userKey '{}' is a UUID. findById result: {}", userKey, userOpt);
        } catch (IllegalArgumentException e) {
            // UUID 변환 실패 → username으로 조회
            userOpt = userRepository.findByUsername(userKey);
            log.debug("userKey '{}' is not UUID. findByUsername result: {}", userKey, userOpt);
        }

        if (userOpt.isEmpty()) {
            log.error("사용자를 찾을 수 없습니다. userKey: {}", userKey);
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }
        return new CustomUserDetails(userOpt.get());
    }
}
