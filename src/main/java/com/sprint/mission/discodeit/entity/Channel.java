package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;

@Getter
public class Channel extends BaseEntity {

    private ChannelType type;
    private String name;
    private String description;

    public Channel(ChannelType type, String name, String description) {
        super();
        this.type = type;
        this.name = name;
        this.description = description;
    }

    // 채널 이름 변경
    public void updateChannel(ChannelType type,String newName, String newDescription) {
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            updateTimestamp();
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            updateTimestamp();
        }
    }
}
