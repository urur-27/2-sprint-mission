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

// Simple factory 형식
// 객체 생성을 관리하는 팩토리 패턴
public class ServiceFactory {
    private static volatile ServiceFactory instance; // 완전히 초기화되지 않은 객체를 참조하지 않도록 volatile로 순서 보장

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    private static final String serviceType = "FILE"; // "FILE" 또는 "JCF" 변경 가능

    // 싱글톤 패턴을 사용하여 여러 인스턴스가 생성되는 상황 방지
    private ServiceFactory() {
        this.userRepository = createUserRepository();
        this.channelRepository = createChannelRepository();
        this.messageRepository = createMessageRepository();

        this.userService = BasicUserService.getInstance(userRepository);
        this.channelService = BasicChannelService.getInstance(channelRepository);
        this.messageService = BasicMessageService.getInstance(getUserService(),getChannelService(),messageRepository);
    }

    public static synchronized ServiceFactory getInstance() {
        // 첫 번째 null 체크 (성능 최적화)
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                // 두 번째 null 체크 (동기화 구간 안에서 중복 생성 방지)
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }


    private static UserRepository createUserRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFUserRepository();
            default:
                return new FileUserRepository();
        }
    }

    private static ChannelRepository createChannelRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFChannelRepository();
            default:
                return new FileChannelRepository();
        }
    }

    private static MessageRepository createMessageRepository() {
        switch (serviceType) {
            case "JCF":
                return new JCFMessageRepository();
            default:
                return new FileMessageRepository();
        }
    }

    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}