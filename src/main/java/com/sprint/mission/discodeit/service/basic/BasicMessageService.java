package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.MessageResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
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
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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
  private final BinaryContentStorage binaryContentStorage;
  private final MessageMapper messageMapper;

  @Override
  @Transactional
  public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
    // 요청받은 id를 가진 Author, Channel이 있는지
    User user = userRepository.findById(request.authorId())
        .orElseThrow(() -> new RestException(ResultCode.USER_NOT_FOUND));

    Channel channel = channelRepository.findById(request.channelId())
        .orElseThrow(() -> new RestException(ResultCode.CHANNEL_NOT_FOUND));

    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile attachment : attachments) {
        BinaryContent binaryContent = multipartFileMapper.toEntity(attachment);
        BinaryContent saved = binaryContentRepository.save(binaryContent);
        try {
          binaryContentStorage.put(saved.getId(), attachment.getBytes());
        } catch (IOException e) {
          throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
        }
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
        .orElseThrow(() -> new RestException(ResultCode.MESSAGE_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable) {
    return messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
  }

  @Override
  @Transactional
  public Message update(UUID messageId, MessageUpdateRequest request) {
    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> new RestException(ResultCode.MESSAGE_NOT_FOUND));
    // Dirty checking
    message.updateMessage(request.newContent());
    return message;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    Message message = messageRepository.findById(id)
        .orElseThrow(() -> new RestException(ResultCode.MESSAGE_NOT_FOUND));

    // 첨부파일 직접 삭제 (Cascade 설정이 없음). 설정 변경 고민
    binaryContentRepository.deleteAll(message.getAttachments());

    messageRepository.delete(message);
  }
}
