
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {

    // 저장 또는 업데이트 (Create/Update)
    void upsert(UserStatus userStatus);

    // 특정 사용자가 온라인인지 확인
    boolean isUserOnline(UUID userId);

    // 온라인 상태인 사용자 목록 조회
    List<UserStatus> findAllOnlineUsers();

    // 특정 사용자의 마지막 접속 시간 업데이트
    void updateLastAccessedAt(UUID userId, Instant lastAccessedAt);

    // 특정 사용자의 상태 삭제 (탈퇴 등)
    void deleteByUserId(UUID userId);
}
