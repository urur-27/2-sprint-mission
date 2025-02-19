package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    private UUID id; // 객체를 식별하기 위한 id
    private long createdAt; // 객체의 생성 시간
    private long updateAt; // 객체의 수정 시간

    public BaseEntity() {
        this.id = UUID.randomUUID(); // 무작위 UUID로 생성자에서 초기화
        this.createdAt = System.currentTimeMillis(); // 생성시간
        this.updateAt = System.currentTimeMillis(); // 생성된 시간으로 업데이트
    }

    // getter
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    // update
    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }


}
