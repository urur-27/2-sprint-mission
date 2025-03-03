package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    //CRUD 기능을 선언
    UUID createChannel(String name);
    Channel getChannelById(UUID id);
    List<Channel> getAllChannels();
    void updateChannel(UUID id, String name);
    void deleteChannel(UUID id);
}
