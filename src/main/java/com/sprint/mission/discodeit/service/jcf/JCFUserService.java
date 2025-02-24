package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // 필드를 활용해 생성, 조회, 수정, 삭제 메소드 제작
    // 유저 생성(이름, 상태)
    @Override
    public void createUser(String username, String email) {
        User user = new User(username, email);
        data.put(user.getId(), user);
    }

    // UUID 기반 유저 조회
    @Override
    public User getUserById(UUID id) {
        return data.get(id);
    }

    // 모든 유저 조회
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(data.values());
    }

    // UUID를 기반으로 수정
    @Override
    public void updateUser(UUID id, String username, String email) {
        User user = data.get(id);
        if (user != null) {
            user.updateUser(username, email);
        }
    }

    // 삭제
    @Override
    public void deleteUser(UUID id) {
        data.remove(id);
    }

}
