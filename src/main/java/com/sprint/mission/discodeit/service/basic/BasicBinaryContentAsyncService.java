package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentUploadStatus;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentAsyncService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicBinaryContentAsyncService implements BinaryContentAsyncService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    @Async("contextAwareTaskExecutor")
    @Transactional
    public void uploadFile(UUID binaryContentId, byte[] bytes) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
                .orElseThrow(() -> new DiscodeitException(ErrorCode.BINARY_CONTENT_NOT_FOUND));

        try {
            // 업로드 시도
            binaryContentStorage.put(binaryContentId, bytes);
            binaryContent.setUploadStatus(BinaryContentUploadStatus.SUCCESS);
        } catch (Exception e) {
            binaryContent.setUploadStatus(BinaryContentUploadStatus.FAILED);
            // 추후 실패 기록 추가 가능
            throw e;
        }
    }}
