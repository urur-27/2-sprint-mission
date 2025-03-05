package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class ServiceFactory {
    // Simple factory 형식
    // 객체 생성을 관리하는 팩토리 패턴
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    public static UserService getUserService() {
        if (userService == null) {
//            userService = JCFUserService.getInstance();
              userService = FileUserService.getInstance();
        }
        return userService;
    }

    public static ChannelService getChannelService() {
        if (channelService == null) {
//            channelService = JCFChannelService.getInstance();
            channelService = FileChannelService.getInstance();
        }
        return channelService;
    }

    public static MessageService getMessageService() {
        if (messageService == null) {
//            messageService = JCFMessageService.getInstance(
//                    getUserService(),
//                    getChannelService()
//            );
            messageService = FileMessageService.getInstance(
                    getUserService(),
                    getChannelService()
            );
        }
        return messageService;
    }
}
