package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  // 채널에 메시지를 보낼 수 있는 기능
  // 해당 채널에 들어가면 "누구누구 : 메시지" 이런 식으로 대화가 표현되게
  // 보낸 User의 정보와 Channel 정보 포함
  private String content;

  @ManyToOne
  @JoinColumn(name = "channel_id", nullable = false, foreignKey = @ForeignKey(name = "fk_messages_channel"))
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "author_id", foreignKey = @ForeignKey(name = "fk_messages_author"))
  private User author;

  @ManyToMany
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  @Builder
  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  // 메시지 수정 메서드
  public void updateMessage(String content) {
    this.content = content;
  }

}