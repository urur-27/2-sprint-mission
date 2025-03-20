package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicReadStatusService implements ReadStatusService {

    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    public UUID create(ReadStatusCreateRequest request) {
        // 관련된 User와 Channel이 존재하는지 확인
        Optional.ofNullable(userRepository.findById(request.userId()))
                .orElseThrow(() -> new NoSuchElementException("user not found with id: " + request.userId()));
        Optional.ofNullable(channelRepository.findById(request.channelId()))
                .orElseThrow(() -> new NoSuchElementException("channel not found with id: " + request.channelId()));

        // 중복 방지
        readStatusRepository.findAllByUser(request.userId()).forEach(r -> {
                    if (r.getChannelId().equals(request.channelId())) {
                        throw new IllegalArgumentException("read status already exists with channel id: " + request.channelId());
                    }
        });

        ReadStatus newReadStatus = new ReadStatus(request.userId(), request.channelId(), request.lastReadAt());
        readStatusRepository.upsert(newReadStatus);
        return newReadStatus.getId();
    }

    // ID로 ReadStatus 조회
    @Override
    public ReadStatus find(UUID readStatusId) {
        return Optional.ofNullable(readStatusRepository.findById(readStatusId))
                .orElseThrow(() -> new NoSuchElementException("read status not found with id: " + readStatusId));
    }

    // 사용자의 Id로 조회
    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUser(userId);
    }

    // ReadStatus 업데이트
    @Override
    public void update(ReadStatusUpdateRequest request) {
        // 기존 정보 조회
        ReadStatus readStatus = readStatusRepository.findById(request.readStatusId());
        if (readStatus == null) {
            throw new NoSuchElementException("read status not found with id: " + request.readStatusId());
        }

        // 정보 업데이트
        readStatusRepository.updateLastReadAt(request.userId(), request.channelId(), request.lastReadAt());
    }

    // ReadStatus 삭제
    @Override
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId);
        if (readStatus == null) {
            throw new NoSuchElementException("read status not found with id: " + readStatusId);
        }

        readStatusRepository.delete(readStatusId);
    }
}