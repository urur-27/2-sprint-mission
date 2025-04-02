package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.dto2.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    //CRUD 기능을 선언
    UUID createPrivateChannel(PrivateChannelCreateRequest request);
    UUID createPublicChannel(PublicChannelCreateRequest request);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAllByUserId(UUID id);
    void update(ChannelUpdateRequest request);
    void delete(UUID id);
}
