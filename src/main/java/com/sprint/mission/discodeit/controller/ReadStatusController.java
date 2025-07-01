package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.util.LogUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;
  private final ReadStatusMapper readStatusMapper;

  // 1. 특정 채널의 메시지 수신 정보 생성
  @PostMapping
  public ResponseEntity<ReadStatusResponse> createReceipts(
      @Valid @RequestBody ReadStatusCreateRequest request) {
    String traceId = MDC.get("traceId");
    UUID userId = request.userId();
    UUID channelId = request.channelId();

    // 시작 로그
    log.info("[CREATE] status=START, userId={}, channelId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId),
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId),
        traceId);

    ReadStatus readStatus = readStatusService.create(request);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, readStatusId={}, traceId={}",
        LogUtils.maskUUID(readStatus.getId()), traceId);

    return ResponseEntity.status(HttpStatus.CREATED).body(readStatusMapper.toResponse(readStatus));
  }

  // 2. 특정 채널의 메시지 수신 정보 수정
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatusResponse> updateReceipts(
      @PathVariable UUID readStatusId,
      @Valid @RequestBody ReadStatusUpdateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, readStatusId={}, traceId={}",
        log.isDebugEnabled() ? readStatusId : LogUtils.maskUUID(readStatusId), traceId);

    ReadStatus updated = readStatusService.update(readStatusId, request);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, readStatusId={}, traceId={}",
        LogUtils.maskUUID(updated.getId()), traceId);

    return ResponseEntity.ok(readStatusMapper.toResponse(updated));
  }

  // 3. 특정 사용자의 메시지 수신 정보 조회
  @GetMapping
  public ResponseEntity<List<ReadStatusResponse>> getAllByUserId(@RequestParam UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    List<ReadStatusResponse> responses = readStatuses.stream()
        .map(readStatusMapper::toResponse)
        .toList();


    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userId={}, readStatusCount={}, traceId={}",
        LogUtils.maskUUID(userId), readStatuses.size(), traceId);

    return ResponseEntity.ok(responses);
  }
}
