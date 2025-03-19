package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;

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
