
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {


  // 특정 사용자가 온라인인지 확인
  @Query("SELECT CASE WHEN COUNT(us) > 0 THEN true ELSE false END FROM UserStatus us WHERE us.user.id = :userId AND us.lastActiveAt > :threshold")
  boolean isUserOnline(@Param("userId") UUID userId, @Param("threshold") Instant threshold);

  Optional<UserStatus> findByUserId(UUID userId);

  // 온라인 상태인 사용자 목록 조회
  @Query("SELECT us FROM UserStatus us WHERE us.lastActiveAt > :threshold")
  List<UserStatus> findAllOnlineUsers(@Param("threshold") Instant threshold);

//  // 특정 사용자의 마지막 접속 시간 업데이트
//  void updateLastAccessedAt(UUID userId, Instant lastAccessedAt);

//  // 특정 사용자의 상태 삭제 (탈퇴 등)
//  @Modifying
//  @Query("DELETE FROM UserStatus us WHERE us.user.id = :userId")
//  void deleteByUserId(@Param("userId") UUID userId);
}
