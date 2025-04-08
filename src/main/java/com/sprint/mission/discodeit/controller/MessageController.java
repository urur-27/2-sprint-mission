package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
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

  // 메시지 보내기
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Message> sendMessage(
      @RequestPart MessageCreateRequest messageCreateRequest,
      @RequestPart(required = false) List<MultipartFile> attachments) {

    Message message = messageService.create(messageCreateRequest, attachments);
    return ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  // 메시지 수정
  @PatchMapping("/{messageId}")
  public ResponseEntity<Message> updateMessage(
      @PathVariable UUID messageId,
      @RequestBody MessageUpdateRequest messageUpdateRequest) {

    Message updatedMessage = messageService.update(messageId, messageUpdateRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(updatedMessage);
  }

  // 메시지 삭제
  @DeleteMapping("/{messageId}")
  public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
    messageService.delete(messageId);
    return ResponseEntity.noContent().build();
  }
  
  // 특정 채널 메시지 목록 조회
  @GetMapping
  public ResponseEntity<List<Message>> getMessagesByChannel(
      @RequestParam UUID channelId) {
    List<Message> messages = messageService.findAllByChannelId(channelId);
    return ResponseEntity.ok(messages);
  }
}
