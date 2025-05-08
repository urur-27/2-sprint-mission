package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "read_statuses", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"channelId", "userId"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
// 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용합니다.
public class ReadStatus extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_read_status_user", foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false,
      foreignKey = @ForeignKey(name = "fk_read_status_channel", foreignKeyDefinition = "FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE"))
  private Channel channel;

  @Column(name = "last_read_at", nullable = false)
  private Instant lastReadAt;

  @Builder
  public ReadStatus(User user, Channel channel, Instant lastReadAt) {
    this.user = user;
    this.channel = channel;
    this.lastReadAt = lastReadAt;
  }

  public void updateReadStatus(Instant readAt) {
    this.lastReadAt = readAt;
  }
}