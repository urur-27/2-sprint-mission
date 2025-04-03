package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {

  private static final long serialVersionUID = 1L;
  private UUID id;
  private Instant createdAt;

  private String fileName;
  private Long size; // 파일 크기 (bytes)
  private String contentType; // 파일의 MIME 타입 (예: image/png)
  private byte[] bytes; // 실제 데이터 (효율적인 저장을 위해 변경)

  public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    //
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }
}