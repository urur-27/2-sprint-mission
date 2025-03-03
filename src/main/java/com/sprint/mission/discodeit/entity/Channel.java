package com.sprint.mission.discodeit.entity;

public class Channel extends BaseEntity {
    // 채널 명
    private String name;

    public Channel(String name) {
        super();
        this.name = name;
    }

    // Getter 메서드
    public String getName() {
        return name;
    }

    // 채널 이름 변경
    public void updateChannel(String name) {
        this.name = name;
        updateTimestamp();
    }
}
