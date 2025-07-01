package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import com.sprint.mission.discodeit.util.AuthUtils;
import com.sprint.mission.discodeit.util.LogUtils;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

  private final MessageRepository messageRepository;
  private final ChannelRepository channelRepository;
  private final UserRepository userRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final MultipartFileMapper multipartFileMapper;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public Message create(MessageCreateRequest request, List<MultipartFile> attachments) {
    String traceId = MDC.get("traceId");
    UUID authorId = request.authorId();
    UUID channelId = request.channelId();

    // 시작 로그
    log.info("[CREATE] status=START, authorId={}, channelId={}, traceId={}",
        log.isDebugEnabled() ? authorId : LogUtils.maskUUID(authorId),
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId),
        traceId);

    // 요청받은 id를 가진 Author, Channel이 있는지
    User user = userRepository.findById(authorId)
        .orElseThrow(() -> {
          log.warn("[CREATE] User not found: userId={}, traceId={}",
              LogUtils.maskUUID(authorId), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    Channel channel = channelRepository.findById(channelId)
        .orElseThrow(() -> {
          log.warn("[CREATE] Channel not found: channelId={}, traceId={}",
              LogUtils.maskUUID(channelId), traceId);
          return new RestException(ResultCode.CHANNEL_NOT_FOUND);
        });

    // 첨부 파일 처리
    List<BinaryContent> binaryContents = new ArrayList<>();
    if (attachments != null) {
      for (MultipartFile attachment : attachments) {
        String fileName = attachment.getOriginalFilename();
        log.info("[CREATE] status=START, processing attachment: fileName={}, traceId={}",
            log.isDebugEnabled() ? fileName : LogUtils.maskFileName(fileName), traceId);

        BinaryContent binaryContent = multipartFileMapper.toEntity(attachment);
        BinaryContent saved = binaryContentRepository.save(binaryContent);
        try {
          binaryContentStorage.put(saved.getId(), attachment.getBytes());
          log.info("[CREATE] status=SUCCESS, contentId={}, traceId={}",
              saved.getId(), traceId);
        } catch (IOException e) {
          log.error("[ERROR] Failed to store BinaryContent: contentId={}, traceId={}",
              saved.getId(), traceId, e);
          throw new RestException(ResultCode.FILE_PROCESSING_ERROR);
        }
        binaryContents.add(saved); // 저장된 객체 사용
      }
    }

    Message message = Message.builder()
        .content(request.content())
        .channel(channel)
        .author(user)
        .attachments(binaryContents)
        .build();

    messageRepository.save(message);

    // 성공 로그
    log.info("[CREATE] status=SUCCESS, messageId={}, attachmentCount={}, traceId={}",
        message.getId(), binaryContents.size(), traceId);
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public Message findById(UUID id) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND] status=START, messageId={}, traceId={}",
        log.isDebugEnabled() ? id : LogUtils.maskUUID(id), traceId);

    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("[FIND] Message not found: messageId={}, traceId={}",
              LogUtils.maskUUID(id), traceId);
          return new RestException(ResultCode.MESSAGE_NOT_FOUND);
        });

    // 성공 로그
    log.info("[FIND] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(id), traceId);
    return message;
  }

  @Override
  @Transactional(readOnly = true)
  public Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[FIND_ALL] status=START, channelId={}, traceId={}",
        log.isDebugEnabled() ? channelId : LogUtils.maskUUID(channelId), traceId);

    Slice<Message> messages = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId,
        pageable);

    // 성공 로그
    log.info("[FIND_ALL] status=SUCCESS, channelId={}, messageCount={}, traceId={}",
        LogUtils.maskUUID(channelId), messages.getNumberOfElements(), traceId);
    return messages;
  }

  @Override
  @Transactional
  public Message update(UUID messageId, MessageUpdateRequest request) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[UPDATE] status=START, messageId={}, traceId={}",
        log.isDebugEnabled() ? messageId : LogUtils.maskUUID(messageId), traceId);

    Message message = messageRepository.findById(messageId)
        .orElseThrow(() -> {
          log.warn("[UPDATE] Message not found: messageId={}, traceId={}",
              LogUtils.maskUUID(messageId), traceId);
          return new RestException(ResultCode.MESSAGE_NOT_FOUND);
        });

    // Dirty checking
    message.updateMessage(request.newContent());

    User currentUser = AuthUtils.getCurrentUser();
    // 본인의 메시지만 수정 가능
    if (!message.getAuthor().getId().equals(currentUser.getId())) {
      throw new RestException(ResultCode.ACCESS_DENIED);
    }

    // 성공 로그
    log.info("[UPDATE] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(messageId), traceId);
    return message;
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    String traceId = MDC.get("traceId");

    // 시작 로그
    log.info("[DELETE] status=START, messageId={}, traceId={}",
        log.isDebugEnabled() ? id : LogUtils.maskUUID(id), traceId);

    Message message = messageRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("[DELETE] Message not found: messageId={}, traceId={}",
              LogUtils.maskUUID(id), traceId);
          return new RestException(ResultCode.MESSAGE_NOT_FOUND);
        });

    // 메세지를 작성한 사람 또는 ROLE_ADMIN 권한을 가진 사용자만 호출할 수 있도록
    User currentUser = AuthUtils.getCurrentUser();
    boolean isOwner = message.getAuthor().getId().equals(currentUser.getId());
    boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
    if (!(isOwner || isAdmin)) {
      throw new RestException(ResultCode.ACCESS_DENIED);
    }



    List<UUID> attachmentIds = message.getAttachments().stream()
        .map(BinaryContent::getId)
        .toList();

    // Chunk 단위로 삭제 (100개씩)
    int chunkSize = 100;
    for (int i = 0; i < attachmentIds.size(); i += chunkSize) {
      List<UUID> chunk = attachmentIds.subList(i, Math.min(i + chunkSize, attachmentIds.size()));
      binaryContentRepository.deleteInBatch(chunk);
      log.info("[DELETE] Deleted attachment chunk: chunkSize={}, traceId={}", chunk.size(),
          traceId);
    }

    messageRepository.delete(message);
    // 성공 로그
    log.info("[DELETE] status=SUCCESS, messageId={}, traceId={}",
        LogUtils.maskUUID(id), traceId);
  }
}
