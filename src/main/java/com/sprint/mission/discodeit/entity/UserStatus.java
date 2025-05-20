package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "user_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true,
      foreignKey = @ForeignKey(name = "fk_user_status_user_id",
          foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  @Builder
  public UserStatus(User user, Instant lastActiveAt) {
    this.user = user;
    this.lastActiveAt = lastActiveAt;
  }

  // 현재 시간으로부터 5분 이내 접속이면 접속 중으로 판단
  public boolean isCurrentOnline() {
    return lastActiveAt.isAfter(Instant.now().minusSeconds(300)); // 5분 (300초)
  }

  public void updateLastAccessedAt(Instant accessedAt) {
    this.lastActiveAt = accessedAt;
  }

  // 온라인으로 상태 변환
  public void setOnline() {
    this.lastActiveAt = Instant.now();
  }
}