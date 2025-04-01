package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
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
    public ResponseEntity<ApiResponse<UUID>> createPublicChannel(@RequestBody PublicChannelCreateRequest request) {
        UUID id = channelService.createPublicChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Public channel \"" + request.name() + "\" has been created.", id));
    }

    // 비공개 채널 생성
    @RequestMapping(value = "/private", method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<UUID>> createPrivateChannel(@RequestBody PrivateChannelCreateRequest request) {
        UUID id = channelService.createPrivateChannel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Private channel has been created for userIds: " + request.userIds(), id));
    }

    // 공개 채널 정보 수정
    @RequestMapping(value = "/public/", method = RequestMethod.PUT)
    public ResponseEntity<ApiResponse<Void>> updatePublicChannel(@RequestBody ChannelUpdateRequest request) {
        channelService.update(request);
        return ResponseEntity.ok(new ApiResponse<>("Public channel has been updated.", null));
    }

    // 채널 삭제
    @RequestMapping(value = "/{channelId}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteChannel(@PathVariable UUID channelId) {
        channelService.delete(channelId);
        return ResponseEntity.ok(new ApiResponse<>("Channel has been deleted.", null));
    }

    // 특정 사용자가 볼 수 있는 채널 목록 조회
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<List<ChannelResponse>>> getChannelsForUser(@PathVariable UUID userId) {
        List<ChannelResponse> channels = channelService.findAllByUserId(userId);
        ApiResponse<List<ChannelResponse>> response = new ApiResponse<>("Successfully viewed channel list", channels);
        return ResponseEntity.ok(response);
    }
}
