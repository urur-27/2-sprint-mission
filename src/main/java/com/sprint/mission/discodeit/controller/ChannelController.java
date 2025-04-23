package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

  private final ChannelService channelService;
  private final ChannelMapper channelMapper;

  // 공개 채널 생성
  @PostMapping("/public")
  public ResponseEntity<ChannelResponse> createPublicChannel(
      @RequestBody PublicChannelCreateRequest request) {
    Channel createdChannel = channelService.createPublicChannel(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelMapper.toResponse(createdChannel));
  }

  // 비공개 채널 생성
  @PostMapping("/private")
  public ResponseEntity<ChannelResponse> createPrivateChannel(
      @RequestBody PrivateChannelCreateRequest request) {
    Channel createdChannel = channelService.createPrivateChannel(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(channelMapper.toResponse(createdChannel));
  }

  // 공개 채널 정보 수정
  @PatchMapping("/{channelId}")
  public ResponseEntity<ChannelResponse> updatePublicChannel(
      @PathVariable UUID channelId,
      @RequestBody PublicChannelUpdateRequest request) {
    Channel updatedChannel = channelService.update(channelId, request);
    return ResponseEntity.ok(channelMapper.toResponse(updatedChannel));
  }

  // 채널 삭제
  @DeleteMapping("/{channelId}")
  public ResponseEntity<Void> deleteChannel(@PathVariable UUID channelId) {
    channelService.delete(channelId);
    return ResponseEntity.noContent().build();
  }

  // 특정 사용자가 볼 수 있는 채널 목록 조회
  @GetMapping
  public ResponseEntity<List<ChannelResponse>> getChannelsForUser(
      @RequestParam UUID userId) {
    List<Channel> channels = channelService.findAllByUserId(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(channels.stream().map(channelMapper::toResponse).toList());
  }
}
