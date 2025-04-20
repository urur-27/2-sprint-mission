package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.dto2.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;
  private final MessageMapper messageMapper;

  // 메시지 보내기
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MessageResponse> sendMessage(
      @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments) {

    Message message = messageService.create(messageCreateRequest, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toResponse(message));
  }

  // 메시지 수정
  @PatchMapping("/{messageId}")
  public ResponseEntity<MessageResponse> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest) {

    Message updatedMessage = messageService.update(messageId, messageUpdateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(messageMapper.toResponse(updatedMessage));
  }

  // 메시지 삭제
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }

  // 특정 채널 메시지 목록 조회 (페이지네이션 적용). api-docs_1.1 스펙에 맞추어서 변환// Pageable을 통해 page, size, sort 정보를 자동으로 추출
  @GetMapping
  public ResponseEntity<PageResponse<MessageResponse>> getMessagesByChannel(
      @RequestParam UUID channelId,
      @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    Slice<Message> messages = messageService.findAllByChannelId(channelId, pageable);

    List<MessageResponse> messageResponses = messages.stream()
        .map(messageMapper::toResponse)
        .toList();

    return ResponseEntity.ok(PageResponseMapper.fromSlice(
        new SliceImpl<>(messageResponses, pageable, messages.hasNext())
    ));
  }

}
