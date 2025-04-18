package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import java.util.UUID;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "binary_contents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BinaryContent extends BaseEntity {

  @Column(name = "file_name", length = 255, nullable = false)
  private String fileName;

  @Column(name = "size", nullable = false)
  private Long size; // 파일 크기 (bytes)

  @Column(name = "content_type", length = 100, nullable = false)
  private String contentType; // 파일의 MIME 타입 (예: image/png)

  @Column(name = "bytes", nullable = false, columnDefinition = "BYTEA")
  private byte[] bytes; // 실제 데이터 (효율적인 저장을 위해 변경)

  public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
    this.fileName = fileName;
    this.size = size;
    this.contentType = contentType;
    this.bytes = bytes;
  }
}