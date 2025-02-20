package com.sprint.mission.discodeit.entity;

public class User extends BaseEntity {
    // 유저 이름과 상태
    private String username;
    private String state;

    public User(String username, String state) {
        super(); // BaseEntity 생성자 호출
        this.username = username;
        this.state = state;
    }

    // Getter 메서드
    public String getusername() {
        return username;
    }

    public String getstate() {
        return state;
    }

    // 업데이트 메서드
    public void updateUser(String username, String state) {
        this.username = username;
        this.state = state;
        updateTimestamp(); // updatedAt 변경
    }
}
