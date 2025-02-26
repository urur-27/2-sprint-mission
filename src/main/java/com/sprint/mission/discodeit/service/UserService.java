package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //CRUD 기능을 선언
    UUID createUser(String username, String email); // user 생성
    User getUserById(UUID id); // 읽기
    List<User> getAllUsers(); // 모두 읽어서 리스트 형으로 가져오기
    void updateUser(UUID id, String username, String email); // 수정
    void deleteUser(UUID id); // 삭제
}