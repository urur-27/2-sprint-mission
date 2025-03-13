package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;


public class BasicChannelService implements ChannelService {
    private static volatile BasicChannelService instance;
    private final ChannelRepository channelRepository;

    // 생성자를 통해 저장소 주입받기
    private BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // 다른 저장소를 주입 받을 수 있도록 getInstance 오버로딩
    public static BasicChannelService getInstance(ChannelRepository channelRepository) {
        if (instance == null) {
            synchronized (BasicChannelService.class) {
                if (instance == null) {
                    instance = new BasicChannelService(channelRepository);
                }
            }
        }
        return instance;
    }
    @Override
    public UUID create(String channelName) {
        Channel channel = new Channel(channelName);
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
    public void update(UUID id, String channelName) {
        channelRepository.update(id, channelName);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.delete(id);
    }
}
