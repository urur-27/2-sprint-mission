package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.dto2.response.PageResponse;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Comparator;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
      Pageable pageable) {

    List<Message> allMessages = messageService.findAllByChannelId(channelId);
    List<MessageResponse> allResponses = allMessages.stream()
        .map(messageMapper::toResponse)
        .collect(Collectors.toList());

    // Comparator을 이용해 MessageResponse의 createAt() 값을 기준으로 정렬하는 기준 제작
    Comparator<MessageResponse> comparator = Comparator.comparing(MessageResponse::createdAt);
    if (pageable.getSort().stream().anyMatch(order ->
        order.getProperty().equals("createdAt") && order.getDirection().isDescending())) {
      comparator = comparator.reversed();
    }
    allResponses.sort(comparator);

    // 페이지네이션: MessageResponse 리스트에서 수행
    int page = pageable.getPageNumber();
    int size = pageable.getPageSize();
    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, allResponses.size());

    List<MessageResponse> paginated;
    if (fromIndex >= allResponses.size()) {
      paginated = List.of(); // 빈 리스트 반환
    } else {
      paginated = allResponses.subList(fromIndex, toIndex);
    }
    boolean hasNext = toIndex < allMessages.size();

    // MessageResponse로 생성
    PageResponse<MessageResponse> pageResponse = new PageResponse<>(
        paginated,
        page,
        size,
        hasNext,
        allResponses.size()
    );

    return ResponseEntity.ok(pageResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    e.printStackTrace(); // 콘솔에 전체 예외 로그 출력
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("에러 발생: " + e.getMessage());
  }
}
