package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    // 직렬화 버전 관리
    private static final long serialVersionUID = 1L;

    // User, Channel, Message에서 공통적으로 다루는 것을 관리하는 class
    private final UUID id; // 객체를 식별하기 위한 id
    private final long createdAt; // 객체의 생성 시간
    private long updatedAt; // 객체의 수정 시간

    public BaseEntity() {
        this.id = UUID.randomUUID(); // 무작위 UUID로 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성시간
        this.updatedAt = System.currentTimeMillis(); // 생성된 시간으로 업데이트
    }

    // updateAt 갱신 메서드
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}
