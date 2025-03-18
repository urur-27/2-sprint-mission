package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    // 유저 이름과 이메일
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password) {
        super(); // BaseEntity 생성자 호출
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // 업데이트 메서드
    public void updateUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        updateTimestamp(); // updatedAt 변경
    }
}
