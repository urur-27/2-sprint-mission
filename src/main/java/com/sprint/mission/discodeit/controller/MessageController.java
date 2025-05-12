package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.dto2.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.LogUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;
  private final MessageMapper messageMapper;

  // 메시지 보내기
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> sendMessage(
      @Valid @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[CREATE] status=START, authorId={}, channelId={}, traceId={}",
        log.isDebugEnabled() ? messageCreateRequest.authorId()
            : LogUtils.maskUUID(messageCreateRequest.authorId()),
        log.isDebugEnabled() ? messageCreateRequest.channelId()
            : LogUtils.maskUUID(messageCreateRequest.channelId()),
        traceId);

    // 메시지 생성
    Message message = messageService.create(messageCreateRequest, attachments);
    MessageResponse response = messageMapper.toResponse(message);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(message.getId()), traceId);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  // 메시지 수정
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> updateMessage(
      @PathVariable UUID messageId,
      @Valid @RequestBody MessageUpdateRequest messageUpdateRequest) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, messageId={}, traceId={}",
        log.isDebugEnabled() ? messageId : LogUtils.maskUUID(messageId), traceId);

    // 메시지 수정
    Message updatedMessage = messageService.update(messageId, messageUpdateRequest);
    MessageResponse response = messageMapper.toResponse(updatedMessage);

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(updatedMessage.getId()), traceId);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  // 메시지 삭제
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, messageId={}, traceId={}",
        log.isDebugEnabled() ? messageId : LogUtils.maskUUID(messageId), traceId);

    // 메시지 삭제
    messageService.delete(messageId);

    // 성공 로그
    log.info("[DELETE] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(messageId), traceId);

    return ResponseEntity.noContent().build();
  }

  // 특정 채널 메시지 목록 조회 (페이지네이션 적용). api-docs_1.1 스펙에 맞추어서 변환// Pageable을 통해 page, size, sort 정보를 자동으로 추출
  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> getMessagesByChannel(
      @RequestParam UUID channelId,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, channelId={}, traceId={}",
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId), traceId);

    Slice<Message> messages = messageService.findAllByChannelId(channelId, pageable);

    List<MessageResponse> messageResponses = messages.stream()
        .map(messageMapper::toResponse)
        .toList();

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, channelId={}, messageCount={}, traceId={}",
        LogUtils.maskUUID(channelId), messageResponses.size(), traceId);

    return ResponseEntity.ok(PageResponseMapper.fromSlice(
        new SliceImpl<>(messageResponses, pageable, messages.hasNext())
    ));
  }

}
