package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto2.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/readStatuses")
@RequiredArgsConstructor
public class ReadStatusController {

  private final ReadStatusService readStatusService;

  // 1. 특정 채널의 메시지 수신 정보 생성
  @PostMapping
  public ResponseEntity<ReadStatus> createReceipts(
      @RequestBody ReadStatusCreateRequest request) {
    ReadStatus readStatus = readStatusService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(readStatus);
  }

  // 2. 특정 채널의 메시지 수신 정보 수정
  @PatchMapping("/{readStatusId}")
  public ResponseEntity<ReadStatus> updateReceipts(
      @PathVariable UUID readStatusId,
      @RequestBody ReadStatusUpdateRequest request) {
    ReadStatus updated = readStatusService.update(readStatusId, request);
    return ResponseEntity.ok(updated);
  }

  // 3. 특정 사용자의 메시지 수신 정보 조회
  @GetMapping
  public ResponseEntity<List<ReadStatus>> getAllByUserId(@RequestParam UUID userId) {
    List<ReadStatus> readStatuses = readStatusService.findAllByUserId(userId);
    return ResponseEntity.ok(readStatuses);
  }
}
