package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.dto2.ChannelResponse;
import com.sprint.mission.discodeit.dto2.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto2.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto2.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    @Override
    public UUID createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.upsert(privateChannel);

        // 참여하는 User별 ReadStatus 생성
        request.userIds().forEach(userId -> {
            ReadStatus readStatus = new ReadStatus(userId, privateChannel.getId(), null);
            readStatusRepository.upsert(readStatus);
        });

        return privateChannel.getId();
    }

    @Override
    public UUID createPublicChannel(PublicChannelCreateRequest request) {
        Channel publicChannel = new Channel(ChannelType.PUBLIC, request.name(), request.description());
        channelRepository.upsert(publicChannel);
        return publicChannel.getId();
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel found for ID: " + id);
        }
        // 해당 채널의 가장 최근 메시지 시간 조회
        Instant lastMessageTime = messageRepository.findAll().stream()
                .filter(msg -> msg.getChannelId().equals(id))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);

        // PRIVATE 채널인 경우 참여한 User ID 조회
        List<UUID> userIds = channel.getType() == ChannelType.PRIVATE
                ? readStatusRepository.findUsersByChannelId(id)
                : List.of();

        return new ChannelResponse(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                lastMessageTime,
                userIds
        );
    }

    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        List<Channel> allChannels = channelRepository.findAll();
        List<ChannelResponse> responses = new ArrayList<>();

        for (Channel channel : allChannels) {
            Instant lastMessageTime = messageRepository.findAll().stream()
                    .filter(msg -> msg.getChannelId().equals(channel.getId()))
                    .map(Message::getCreatedAt)
                    .max(Instant::compareTo)
                    .orElse(null);

            List<UUID> userIds = channel.getType() == ChannelType.PRIVATE
                    ? readStatusRepository.findUsersByChannelId(channel.getId())
                    : List.of();

            // PRIVATE 채널은 참여한 User만 조회 가능
            if (channel.getType() == ChannelType.PRIVATE && !userIds.contains(userId)) {
                continue;
            }

            responses.add(new ChannelResponse(
                    channel.getId(),
                    channel.getType(),
                    channel.getName(),
                    channel.getDescription(),
                    lastMessageTime,
                    userIds
            ));
        }
        return responses;
    }

    @Override
    public void update(ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(request.id());
        if (channel == null) {
            throw new NoSuchElementException("No channel found for ID: " + request.id());
        }

        if (channel.getType() == ChannelType.PRIVATE) {
            throw new UnsupportedOperationException("Private channels cannot be updated.");
        }

        channel.updateChannel(request.type(), request.name(), request.description());
        channelRepository.upsert(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = channelRepository.findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel found for ID: " + id);
        }

        // 관련 도메인 같이 삭제
        messageRepository.delete(id);
        readStatusRepository.deleteByChannelId(id);
        channelRepository.delete(id);
    }
}