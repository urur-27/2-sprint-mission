package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    // 직렬화 버전 관리
    private static final long serialVersionUID = 1L;

    // User, Channel, Message에서 공통적으로 다루는 것을 관리하는 class
    private final UUID id; // 객체를 식별하기 위한 id
    private final Instant createdAt; // 객체의 생성 시간
    private Instant updatedAt; // 객체의 수정 시간

    public BaseEntity() {
        this.id = UUID.randomUUID(); // 무작위 UUID로 생성자에서 초기화
        Instant now = Instant.now();
        this.createdAt = now; // 생성 시간 저장 (UTC 기준)
        this.updatedAt = now; // 생성된 시간으로 업데이트
    }

    // updateAt 갱신 메서드
    protected void updateTimestamp() {
        this.updatedAt = Instant.now();
    }
}
