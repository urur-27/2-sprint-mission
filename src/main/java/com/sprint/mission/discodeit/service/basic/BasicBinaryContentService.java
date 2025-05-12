package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.LogUtils;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {

  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest request) {
    String traceId = MDC.get("traceId");
    String fileName = request.fileName();

    // 시작 로그
    log.info("[CREATE] status=START, fileName={}, traceId={}",
        log.isDebugEnabled() ? fileName : LogUtils.maskFileName(fileName), traceId);

    byte[] bytes = request.bytes();
    String contentType = request.contentType();

    BinaryContent binaryContent = BinaryContent.builder()
        .fileName(fileName)
        .size((long) bytes.length)
        .contentType(contentType)
        .build();

    // 실제 데이터 저장
    try {
      binaryContentStorage.put(binaryContent.getId(), bytes);
      log.info("[CREATE] BinaryContent created successfully: contentId={}, traceId={}",
          binaryContent.getId(), traceId);
    } catch (Exception e) {
      log.error("[ERROR] Failed to store BinaryContent: contentId={}, traceId={}",
          binaryContent.getId(), traceId, e);
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, contentId={}, traceId={}",
        binaryContent.getId(), traceId);
    return binaryContentRepository.save(binaryContent);
  }

  @Override
  @Transactional(readOnly = true)
  public BinaryContent findById(UUID id) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, contentId={}, traceId={}",
        log.isDebugEnabled() ? id : LogUtils.maskUUID(id), traceId);

    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("[FIND] BinaryContent not found: contentId={}, traceId={}",
              LogUtils.maskUUID(id), traceId);
          return new RestException(ResultCode.BINARY_CONTENT_NOT_FOUND);
        });

    // 성공 로그
    log.info("[FIND] status=SUCCESS, contentId={}, traceId={}",
        LogUtils.maskUUID(id), traceId);
    return binaryContent;
  }

  @Override
  @Transactional(readOnly = true)
  public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, contentIds={}, traceId={}",
        log.isDebugEnabled() ? ids : LogUtils.maskUUIDList(ids), traceId);

    List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(ids);

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, contentCount={}, traceId={}",
        contents.size(), traceId);
    return contents;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, contentId={}, traceId={}",
        log.isDebugEnabled() ? id : LogUtils.maskUUID(id), traceId);

    BinaryContent binaryContent = findById(id);
    binaryContentRepository.delete(binaryContent);

    try {
      // 저장소에서도 삭제
      binaryContentStorage.delete(id);
      log.info("[DELETE] BinaryContent deleted successfully: contentId={}, traceId={}",
          id, traceId);
    } catch (Exception e) {
      log.error("[ERROR] Failed to delete BinaryContent from storage: contentId={}, traceId={}",
          id, traceId, e);
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, contentId={}, traceId={}",
        id, traceId);
  }

  public Optional<BinaryContentCreateRequest> resolveProfileRequest(MultipartFile profileFile) {
    String traceId = MDC.get("traceId");

    // 파일이 비어있는지 먼저 체크
    if (profileFile == null || profileFile.isEmpty()) {
      log.info("[RESOLVE] status=SUCCESS, empty profile file, skipping: traceId={}", traceId);
      return Optional.empty();
    }

    // 시작 로그
    log.info("[RESOLVE] status=START, fileName={}, traceId={}",
        log.isDebugEnabled() ? profileFile.getOriginalFilename()
            : LogUtils.maskFileName(profileFile.getOriginalFilename()),
        traceId);

    try {
      BinaryContentCreateRequest request = new BinaryContentCreateRequest(
          profileFile.getOriginalFilename(),
          profileFile.getContentType(),
          profileFile.getBytes()
      );

      // 성공 로그
      log.info("[RESOLVE] status=SUCCESS, fileName={}, traceId={}",
          LogUtils.maskFileName(profileFile.getOriginalFilename()), traceId);
      return Optional.of(request);
    } catch (IOException e) {
      log.error("[ERROR] Failed to read profile file: fileName={}, traceId={}",
          LogUtils.maskFileName(profileFile.getOriginalFilename()), traceId, e);
      throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
    }
  }

}
