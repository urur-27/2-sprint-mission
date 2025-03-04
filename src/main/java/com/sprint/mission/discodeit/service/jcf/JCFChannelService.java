package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    // volatile을 사용하여 변수의 값을 JVM이 캐시하지 않도록 보장
    private static volatile JCFChannelService instance;

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, Channel> data;

    // private 생성자로 외부에서 인스턴스 생성 방지
    private JCFChannelService() {
        this.data = new HashMap<>();
    }

    // 인스턴스를 가져오는 메서드
    public static JCFChannelService getInstance() {
        // 첫 번째 null 체크 (성능 최적화)
        if (instance == null) {
            synchronized (JCFChannelService.class) {
                // 두 번째 null 체크 (동기화 구간 안에서 중복 생성 방지)
                if (instance == null) {
                    instance = new JCFChannelService();
                }
            }
        }
        return instance;
    }

    // 채널 생성
    @Override
    public UUID createChannel(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel.getId();
    }

    // ID를 통한 채널 조회
    @Override
    public Channel getChannelById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("No data for that ID could be found.: " + id));
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
        if (channel == null) {
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
            channel.updateChannel(name);
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
        data.remove(id);
    }

}