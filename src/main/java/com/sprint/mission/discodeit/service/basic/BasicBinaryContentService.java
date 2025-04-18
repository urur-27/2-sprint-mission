package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.notfound.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest request) {
    String fileName = request.fileName();
    byte[] bytes = request.bytes();
    String contentType = request.contentType();
    BinaryContent binaryContent = new BinaryContent(
        fileName,
        (long) bytes.length,
        contentType,
        bytes
    );
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent findById(UUID id) {
    return binaryContentRepository.findById(id)
        .orElseThrow(() -> new BinaryContentNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    return binaryContentRepository.findAllByIdIn(ids);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    BinaryContent binaryContent = findById(id);
    binaryContentRepository.delete(binaryContent);
  }
}
