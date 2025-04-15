package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {

  private UUID userId;
  private Instant lastActiveAt;

  public UserStatus(UUID userId, Instant lastActiveAt) {
    super();
    this.userId = userId;
    this.lastActiveAt = lastActiveAt;
  }

  // 현재 시간으로부터 5분 이내 접속이면 접속 중으로 판단
  public boolean isCurrentOnline() {
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300)); // 5분 (300초)
  }

  public void updateLastAccessedAt(Instant accessedAt) {
    this.lastActiveAt = accessedAt;
  }
}