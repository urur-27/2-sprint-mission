package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notfound.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.FileProcessingException;
import com.sprint.mission.discodeit.exception.notfound.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MessageMapper messageMapper;

  @Override
  public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
    // 요청받은 id를 가진 Author, Channel이 있는지
    User user = userRepository.findById(request.authorId());
    if (user == null) {
      throw new UserNotFoundException(request.authorId());
    }
    Channel channel = channelRepository.findById(request.channelId());
    if (channel == null) {
      throw new ChannelNotFoundException(request.channelId());
    }

    List<UUID> attachmentIds = new ArrayList<>();

    // 첨부 파일 처리
    if (attachments != null) {
      for (MultipartFile attachment : attachments) {
        try {
          BinaryContent binaryContent = new BinaryContent(
              attachment.getOriginalFilename(),
              attachment.getSize(),
              attachment.getContentType(),
              attachment.getBytes()
          );
          UUID attachmentId = binaryContentRepository.upsert(binaryContent).getId();
          attachmentIds.add(attachmentId);
        } catch (IOException e) {
          throw new FileProcessingException("An error occurred while processing the attachment.",
              e);
        }
      }
    }

    Message message = new Message(
        request.content(),
        request.authorId(),
        request.channelId(),
        attachmentIds
    );

    return messageRepository.upsert(message);
  }

  @Override
  public Message findById(UUID id) {
    Message message = messageRepository.findById(id);
    if (message == null) {
      throw new MessageNotFoundException(id);
    }
    return message;
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAll()
        .stream()
        .filter(message -> channelId.equals(message.getChannelId())) // Null-safe: 왼쪽 기준 비교
        .collect(Collectors.toList());
  }

  @Override
  public MessageResponse update(UUID messageId, MessageUpdateRequest request) {
    Message message = findById(messageId);
    if (message == null) {
      throw new MessageNotFoundException(messageId);
    }
    message.updateMessage(request.newContent());
    messageRepository.update(messageId, request.newContent());
    return messageMapper.toResponse(message);
  }

  @Override
  public void delete(UUID id) {
    Message message = messageRepository.findById(id);
    if (message == null) {
      throw new MessageNotFoundException(id);
    }
    message.getAttachmentIds()
        .forEach(binaryContentRepository::delete);

    messageRepository.delete(id);
  }
}
