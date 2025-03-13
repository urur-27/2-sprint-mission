package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void upsert(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, String newChannelname) {
        Channel channel = data.get(id);
        if(channel != null){
            channel.updateChannel(newChannelname);
        }

    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}