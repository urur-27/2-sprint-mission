package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    // 저장 로직을 위한 인터페이스
    void upsert(Channel channel);  // 저장 (JCF: 메모리(Map)에 저장, File: 파일로 저장)
    Channel findById(UUID id);  // ID를 기반으로 찾기
    List<Channel> findAll();  // 모든 데이터 찾기
    void update(UUID id, String newChannelName);
    void delete(UUID id);
}

