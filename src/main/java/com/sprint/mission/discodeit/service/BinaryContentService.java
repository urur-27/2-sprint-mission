package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(BinaryContentCreateRequest request);

  BinaryContent findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
}
