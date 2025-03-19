package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    @Override
    public UUID create(ChannelType type, String channelName, String description) {
        Channel channel = new Channel(type, channelName, description);
        channelRepository.upsert(channel);
        return channel.getId();
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel found for ID: " + id);
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public void update(UUID id, ChannelType type,String channelName, String description) {
        channelRepository.update(id, type, channelName, description);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.delete(id);
    }
}
