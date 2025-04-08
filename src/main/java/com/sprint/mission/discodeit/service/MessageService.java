package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto2.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto2.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  //CRUD 기능을 선언
  Message create(MessageCreateRequest request, List<MultipartFile> attachments);

  Message findById(UUID id);

  List<Message> findAllByChannelId(UUID channelId);

  Message update(UUID messageId, MessageUpdateRequest request);

  void delete(UUID id);
}