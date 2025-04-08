package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatus> data = new HashMap<>();

  @Override
  public ReadStatus upsert(ReadStatus readStatus) {
    data.put(readStatus.getId(), readStatus);
    return readStatus;
  }

  @Override
  public List<UUID> findUsersByChannelId(UUID channelId) {
    return data.values().stream()
        .filter(r -> r.getChannelId().equals(channelId))
        .map(ReadStatus::getUserId)
        .collect(Collectors.toList());
  }

  @Override
  public ReadStatus findById(UUID readStatusId) {
    return data.get(readStatusId);
  }

  @Override
  public List<ReadStatus> findAllByUser(UUID userId) {
    return data.values().stream()
        .filter(r -> r.getUserId().equals(userId))
        .collect(Collectors.toList());
  }

  @Override
  public void updateLastReadAt(UUID userId, UUID channelId, Instant lastReadAt) {
    data.values().stream()
        .filter(rs -> rs.getUserId().equals(userId) && rs.getChannelId().equals(channelId))
        .forEach(rs -> rs.updateReadStatus(lastReadAt));
  }


  @Override
  public void deleteByChannelId(UUID channelId) {
    data.values().removeIf(rs -> rs.getChannelId().equals(channelId));
  }

  @Override
  public void delete(UUID readStatusId) {
    data.remove(readStatusId);
  }
}
