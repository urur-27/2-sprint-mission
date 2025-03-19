package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    //CRUD 기능을 선언
    UUID create(ChannelType type, String channelName, String description);
    Channel findById(UUID id);
    List<Channel> findAll();
    void update(UUID id, ChannelType type, String channelName, String description);
    void delete(UUID id);
}
