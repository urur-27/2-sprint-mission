package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.response.ChannelResponse;
import com.sprint.mission.discodeit.dto2.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.request.PublicChannelCreateRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

  //CRUD 기능을 선언
  ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);

  ChannelResponse createPublicChannel(PublicChannelCreateRequest request);

  ChannelResponse findById(UUID id);

  List<ChannelResponse> findAllByUserId(UUID id);

  ChannelResponse update(UUID channelId, PublicChannelUpdateRequest request);

  void delete(UUID id);
}
