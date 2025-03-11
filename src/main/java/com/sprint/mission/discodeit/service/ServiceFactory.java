package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class ServiceFactory {
    // Simple factory 형식
    // 객체 생성을 관리하는 팩토리 패턴
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    private static final String serviceType = "FILE"; // "FILE" 또는 "JCF" 변경 가능

    private static final UserRepository userRepository = createUserRepository();
    private static final ChannelRepository channelRepository = createChannelRepository();
    private static final MessageRepository messageRepository = createMessageRepository();

    private static UserRepository createUserRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFUserRepository();
            case "FILE":
            default:
                return new FileUserRepository();
        }
    }

    private static ChannelRepository createChannelRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFChannelRepository();
            case "FILE":
            default:
                return new FileChannelRepository();
        }
    }

    private static MessageRepository createMessageRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFMessageRepository();
            case "FILE":
            default:
                return new FileMessageRepository();
        }
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = BasicUserService.getInstance(userRepository);
        }
        return userService;
    }

    public static ChannelService getChannelService() {
        if (channelService == null) {
            channelService = BasicChannelService.getInstance(channelRepository);
        }
        return channelService;
    }

    public static MessageService getMessageService() {
        if (messageService == null) {
            messageService = BasicMessageService.getInstance(
                    getUserService(),
                    getChannelService(),
                    messageRepository
            );
        }
        return messageService;
    }
}