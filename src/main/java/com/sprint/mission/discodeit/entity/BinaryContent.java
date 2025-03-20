package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity {
    private final byte[] data;      // 실제 데이터 (효율적인 저장을 위해 변경)
    private final String contentType; // 파일의 MIME 타입 (예: image/png)
    private final long size; // 파일 크기 (bytes)

    public BinaryContent(byte[] data, String contentType, long size) {
        super();
        this.data = data;
        this.contentType = contentType;
        this.size = size;
    }
}