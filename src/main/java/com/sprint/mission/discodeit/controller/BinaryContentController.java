package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;
  private final BinaryContentStorage binaryContentStorage;

  // 단일 파일 조회
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> findFile(@PathVariable UUID binaryContentId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, contentId={}, traceId={}",
        log.isDebugEnabled() ? binaryContentId : LogUtils.maskUUID(binaryContentId), traceId);

    BinaryContent content = binaryContentService.findById(binaryContentId);
    BinaryContentResponse response = binaryContentMapper.toResponse(content);

    // 성공 로그
    log.info("[FIND] status=SUCCESS, contentId={}, traceId={}",
        LogUtils.maskUUID(binaryContentId), traceId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 여러 파일 조회
  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> findFiles(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, contentIds={}, traceId={}",
        log.isDebugEnabled() ? binaryContentIds : LogUtils.maskUUIDList(binaryContentIds), traceId);

    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentResponse> responses = binaryContents.stream()
        .map(binaryContentMapper::toResponse)
        .toList();

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, contentCount={}, traceId={}",
        responses.size(), traceId);

    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  // 파일 다운로드
  @GetMapping("{binaryContentId}/download")
  public ResponseEntity<?> downloadFile(@PathVariable UUID binaryContentId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DOWNLOAD] status=START, contentId={}, traceId={}",
        log.isDebugEnabled() ? binaryContentId : LogUtils.maskUUID(binaryContentId), traceId);

    BinaryContent content = binaryContentService.findById(binaryContentId);
    BinaryContentResponse response = binaryContentMapper.toResponse(content);
    ResponseEntity<?> downloadResponse = binaryContentStorage.download(response);

    // 성공 로그
    log.info("[DOWNLOAD] status=SUCCESS, contentId={}, traceId={}",
        LogUtils.maskUUID(binaryContentId), traceId);

    return downloadResponse;
  }
}

