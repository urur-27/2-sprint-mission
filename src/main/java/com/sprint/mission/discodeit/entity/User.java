package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    // 유저 이름과 이메일
    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        super(); // BaseEntity 생성자 호출
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }



    // 업데이트 메서드
    public void updateUser(String username, String email, String password, UUID profileId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        updateTimestamp(); // updatedAt 변경
    }
}
