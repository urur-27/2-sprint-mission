package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.security.jwt.JwtSession;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JwtSessionRepository extends JpaRepository<JwtSession, Long> {
    Optional<JwtSession> findByRefreshToken(String refreshToken);
}