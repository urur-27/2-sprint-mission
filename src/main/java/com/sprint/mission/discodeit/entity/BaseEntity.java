package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    // User, Channel, Message에서 공통적으로 다루는 것을 관리하는 class
    private UUID id; // 객체를 식별하기 위한 id
    private final long createdAt; // 객체의 생성 시간
    private long updatedAt; // 객체의 수정 시간

    public BaseEntity() {
        this.id = UUID.randomUUID(); // 무작위 UUID로 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성시간
        this.updatedAt = System.currentTimeMillis(); // 생성된 시간으로 업데이트
    }

    // getter
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdateAt() {
        return updatedAt;
    }

    // update
    public void updateId(UUID id) {
        this.id = id;
    }

    // updateAt 갱신 메서드
    protected void updateTimestamp() {
        this.updatedAt = System.currentTimeMillis();
    }
}
