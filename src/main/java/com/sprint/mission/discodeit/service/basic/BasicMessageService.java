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
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MultipartFileMapper multipartFileMapper;

  @Override
  @Transactional
  public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
    // 요청받은 id를 가진 Author, Channel이 있는지
    User user = userRepository.findById(request.authorId())
        .orElseThrow(() -> new UserNotFoundException(request.authorId()));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new ChannelNotFoundException(request.channelId()));

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile attachment : attachments) {
        BinaryContent binaryContent = multipartFileMapper.toEntity(attachment);
        BinaryContent saved = binaryContentRepository.save(binaryContent);
        binaryContents.add(saved); // 저장된 객체 사용
      }
    }

    Message message = new Message(
        request.content(),
        channel,
        user,
        binaryContents
    );

    return messageRepository.save(message);
  }

  @Override
  @Transactional(readOnly = true)
  public Message findById(UUID id) {
    return messageRepository.findById(id)
        .orElseThrow(() -> new MessageNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Message> findAllByChannelId(UUID channelId) {
    return messageRepository.findAll()
        .stream()
        .filter(message -> channelId.equals(message.getChannel().getId())) // Null-safe: 왼쪽 기준 비교
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new MessageNotFoundException(messageId));
    // Dirty checking
    message.updateMessage(request.newContent());
    return message;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new MessageNotFoundException(id));

    // 첨부파일 직접 삭제 (Cascade 설정이 없음). 설정 변경 고민
    binaryContentRepository.deleteAll(message.getAttachments());

    messageRepository.delete(message);
  }
}
