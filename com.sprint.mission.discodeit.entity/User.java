package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    // 유저 이름과 이메일
    private String username;
    private String email;

    public User(String username, String email) {
        super(); // BaseEntity 생성자 호출
        this.username = username;
        this.email = email;
    }

    // Getter 메서드
    public String getusername() {
        return username;
    }

    public String getemail() {
        return email;
    }

    // 업데이트 메서드
    public void updateUser(String username, String email) {
        this.username = username;
        this.email = email;
        updateTimestamp(); // updatedAt 변경
    }
}
