package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
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
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;

  // 공개 채널 생성
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @Valid @RequestBody PublicChannelCreateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[CREATE] status=START, channelName={}, traceId={}",
        log.isDebugEnabled() ? request.name() : LogUtils.mask(request.name()), traceId);

    // 채널 생성
    Channel createdChannel = channelService.createPublicChannel(request);
    // 생성된 채널의 Response 반환
    ChannelResponse response = channelService.getChannelResponse(createdChannel);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, channelId={}, traceId={}",
        LogUtils.maskUUID(createdChannel.getId()), traceId);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 비공개 채널 생성
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @Valid @RequestBody PrivateChannelCreateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[CREATE] status=START, participantIds={}, traceId={}",
        log.isDebugEnabled() ? request.userIds() : LogUtils.maskUUIDList(request.userIds()),
        traceId);

    Channel createdChannel = channelService.createPrivateChannel(request);
    ChannelResponse response = channelService.getChannelResponse(createdChannel);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, channelId={}, traceId={}",
        LogUtils.maskUUID(createdChannel.getId()), traceId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 공개 채널 정보 수정
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> updatePublicChannel(
      @PathVariable UUID channelId,
      @Valid @RequestBody PublicChannelUpdateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, channelId={}, traceId={}",
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId), traceId);

    Channel updatedChannel = channelService.update(channelId, request);
    ChannelResponse response = channelService.getChannelResponse(updatedChannel);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, channelId={}, traceId={}",
        LogUtils.maskUUID(channelId), traceId);
    return ResponseEntity.ok(response);
  }

  // 채널 삭제
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, channelId={}, traceId={}",
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId), traceId);

    channelService.delete(channelId);

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, channelId={}, traceId={}",
        LogUtils.maskUUID(channelId), traceId);
    return ResponseEntity.noContent().build();
  }

  // 특정 사용자가 볼 수 있는 채널 목록 조회
  @GetMapping
  public ResponseEntity<List<ChannelResponse>> getChannelsForUser(
      @RequestParam UUID userId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, userId={}, traceId={}",
        log.isDebugEnabled() ? userId : LogUtils.maskUUID(userId), traceId);

    List<Channel> channels = channelService.findAllByUserId(userId);
    List<ChannelResponse> responses = channels.stream()
        .map(channelService::getChannelResponse)
        .toList();

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, userId={}, channelCount={}, traceId={}",
        LogUtils.maskUUID(userId), responses.size(), traceId);
    return ResponseEntity.ok(responses);
  }
}
