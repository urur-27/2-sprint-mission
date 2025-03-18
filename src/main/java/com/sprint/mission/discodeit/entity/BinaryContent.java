package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Blob;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity {
    private final UUID userId;    // User 의존관계 (소유자)
    private final UUID messageId; // Message 의존관계 (첨부한 메시지)
    private final byte[] data;      // 실제 데이터 (효율적인 저장을 위해 변경)
    private final String contentType; // 파일의 MIME 타입 (예: image/png)
    private final long size; // 파일 크기 (bytes)

    public BinaryContent(UUID userId, UUID messageId, byte[] data, String contentType, long size) {
        super();
        this.userId = userId;
        this.messageId = messageId;
        this.data = data;
        this.contentType = contentType;
        this.size = size;
    }
}