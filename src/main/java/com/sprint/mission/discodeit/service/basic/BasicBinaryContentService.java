package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
    BinaryContentRepository binaryContentRepository;

    @Override
    public UUID create(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(
                request.data(),
                request.contentType(),
                request.size()
        );
        return binaryContentRepository.upsert(binaryContent);
    }

    @Override
    public BinaryContent findById(UUID id) {
        return binaryContentRepository.findById(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(UUID id) {
        return binaryContentRepository.findAllByIdIn(id);
    }

    @Override
    public void delete(UUID id) {
        binaryContentRepository.delete(id);
    }
}
