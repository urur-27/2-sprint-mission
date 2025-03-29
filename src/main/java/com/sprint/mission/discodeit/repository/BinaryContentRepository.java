
package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {
    // 파일 저장 (Create)
    UUID upsert(BinaryContent binaryContent);
    // 모든 파일 조회 (Read)
    List<BinaryContent> findAll();
    // UUID를 통해 조회
    BinaryContent findById(UUID userId);
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
    void delete(UUID id);
}