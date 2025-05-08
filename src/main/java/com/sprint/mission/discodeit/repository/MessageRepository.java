package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

  List<Message> findByChannelId(UUID channelId);

  Slice<Message> findByChannelIdOrderByCreatedAtDesc(UUID channelId,
      Pageable pageable); // Slice와 JPA를 통해 자동으로 LIMIT, OFFSET, ORDER BY 등을 포함한 쿼리 생성

  Optional<Message> findFirstByChannelIdOrderByCreatedAtDesc(UUID channelId); // 가장 최근 메시지 반환

}

