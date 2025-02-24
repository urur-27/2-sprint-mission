package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private static JCFChannelService instance; // 정적 변수로 유일한 인스턴스 저장

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, Channel> data;

    private JCFChannelService() { // private 생성자로 외부에서 인스턴스 생성 방지
        this.data = new HashMap<>();
    }

    public static JCFChannelService getInstance() {
        if (instance == null) {
            instance = new JCFChannelService();
        }
        return instance;
    }
//    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
//    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
//    private final Map<UUID, Channel> data;
//
//    public JCFChannelService() {
//        this.data = new HashMap<>();
//    }

    // 채널 생성
    @Override
    public void createChannel(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
    }

    // ID를 통한 채널 조회
    @Override
    public Channel getChannelById(UUID id) {
        return data.get(id);
    }

    // 모든 채널 조회 후 List 배열로 정리
    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    // 채널 수정
    @Override
    public void updateChannel(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateChannel(name);
        }
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }
}