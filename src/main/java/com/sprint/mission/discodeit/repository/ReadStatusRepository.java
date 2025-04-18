package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {


  // 특정 채널을 이용중인 모든 사용자 조회
  @Query("SELECT rs.user.id FROM ReadStatus rs WHERE rs.channel.id = :channelId")
  List<UUID> findUsersByChannelId(@Param("channelId") UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  // 특정 사용자의 특정 채널에 대한 마지막 읽은 시간 업데이트
  @Modifying
  @Query("UPDATE ReadStatus rs SET rs.lastReadAt = :lastReadAt WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
  void updateLastReadAt(@Param("userId") UUID userId,
      @Param("channelId") UUID channelId,
      @Param("lastReadAt") Instant lastReadAt);

  // 특정 채널에서 모든 사용자들의 읽기 상태 삭제 (채널 삭제 등)
  @Modifying
  @Query("DELETE FROM ReadStatus rs WHERE rs.channel.id = :channelId")
  void deleteByChannelId(@Param("channelId") UUID channelId);
}