package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.*;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
public class ChannelController {
    private final ChannelService channelService;

    // 공개 채널 생성
    @RequestMapping(value = "/public", method = RequestMethod.POST)
    public ResponseEntity<String> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Public channel \"" + request.name() + "\" has been created.");
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<String> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Private channel has been created.\nuserIds : \"" + request.userIds() + "\".");
    }

    // 공개 채널 정보 수정
    @RequestMapping(value = "/public/", method = RequestMethod.PUT)
    public ResponseEntity<String> updatePublicChannel(@RequestBody ChannelUpdateRequest request) {
        channelService.update(request);
        return ResponseEntity.ok("Public channel has been updated.");
    }

    // 채널 삭제
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.ok("Channel has been deleted.");
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<List<ChannelResponse>>> getChannelsForUser(@PathVariable UUID userId) {
        List<ChannelResponse> channels = channelService.findAllByUserId(userId);
        ApiResponse<List<ChannelResponse>> response = new ApiResponse<>("Successfully viewed channel list", channels);
        return ResponseEntity.ok(response);
    }
}
