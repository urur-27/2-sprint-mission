package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.ApiResponse;
import com.sprint.mission.discodeit.dto2.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 메시지 보내기
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> sendMessage(@RequestBody MessageCreateRequest request) {
        messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Message has been sent.");
    }

    // 메시지 수정
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> updateMessage(@RequestBody MessageUpdateRequest request) {
        messageService.update(request);
        return ResponseEntity.ok("Message has been updated.");
    }

    // 메시지 삭제
    @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMessage(@PathVariable UUID messageId) {
        messageService.delete(messageId);
        return ResponseEntity.ok("Message has been deleted.");
    }

    // 특정 채널 메시지 목록 조회
    @RequestMapping(value = "/channel/{channelId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<List<Message>>> getMessagesByChannel(@PathVariable UUID channelId) {
        List<Message> messages = messageService.findAllByChannelId(channelId);
        ApiResponse<List<Message>> response = new ApiResponse<>("Message list search success", messages);
        return ResponseEntity.ok(response);
    }
}
