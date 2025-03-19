package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReadStatusRepository {

    // 저장 또는 업데이트 (Create/Update)
    void upsert(ReadStatus readStatus);

    // 특정 채널을 이용중인 모든 사용자 조회
    List<UUID> findUsersByChannelId(UUID channelId);

    // 특정 사용자와 채널에 대한 읽기 상태 조회
    ReadStatus findByUserIdAndChannelId(UUID userId, UUID channelId);

    // 특정 사용자의 특정 채널에 대한 마지막 읽은 시간 업데이트
    void updateLastReadAt(UUID userId, UUID channelId, Instant lastReadAt);

    // 특정 사용자의 모든 채널 읽기 상태 삭제 (탈퇴 등)
    void deleteByUserId(UUID userId);

    // 특정 채널에서 모든 사용자들의 읽기 상태 삭제 (채널 삭제 등)
    void deleteByChannelId(UUID channelId);
}