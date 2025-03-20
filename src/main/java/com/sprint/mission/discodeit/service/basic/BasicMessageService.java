package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UUID create(MessageCreateRequest request) {
        // 첨부파일 등록
        List<UUID> attachmentIds = (request.attachments() != null)
                ? request.attachments().stream()
                .map(attachment -> {
                    BinaryContent binaryContent = new BinaryContent(
                            attachment.data(),
                            attachment.contentType(),
                            attachment.size()
                    );
                    return binaryContentRepository.upsert(binaryContent); // 저장 후 UUID 반환
                })
                .collect(Collectors.toList())
                : List.of();

        Message message = new Message(
                request.content(),
                request.senderId(),
                request.channelId(),
                attachmentIds
        );

        messageRepository.upsert(message);

        return message.getId();
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(messageRepository.findById(id))
                .orElseThrow(() -> new NoSuchElementException("No message found for ID: " + id));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageRepository.findAll()
                .stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public void update(MessageUpdateRequest request) {
        Message message = findById(request.id());
        message.updateMessage(request.content());
        messageRepository.update(request.id(), request.content());
    }

    @Override
    public void delete(UUID id) {
        // 첨부파일(BinaryContent) 삭제 로직 추가
        binaryContentRepository.deleteByMessageId(id);
        messageRepository.delete(id);
    }

}
