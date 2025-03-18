package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class UserStatus extends BaseEntity {
    private UUID userId;
    private Instant lastAccessedAt;

    public UserStatus(UUID userId, Instant lastAccessedAt) {
        super();
        this.userId = userId;
        this.lastAccessedAt = lastAccessedAt;
    }

    // 현재 시간으로부터 5분 이내 접속이면 접속 중으로 판단
    public boolean isCurrentOnline() {
        return lastAccessedAt.isAfter(Instant.now().minusSeconds(300)); // 5분 (300초)
    }

    public void updateLastAccessedAt(Instant accessedAt) {
        this.lastAccessedAt = accessedAt;
    }
}