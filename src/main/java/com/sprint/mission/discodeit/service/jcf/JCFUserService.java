package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private static volatile JCFUserService instance;

    // JCF를 이용하여 저장할 수 있는 필드(data)를 final로 선언
    // Key - Value를 이용하여 저장하는 Map이용. 데이터 키 기간으로 검색할 수 있도록
    private final Map<UUID, User> data;

    private JCFUserService() { // private 생성자로 외부에서 인스턴스 생성 방지
        this.data = new HashMap<>();
    }

    // 인스턴스를 가져오는 메서드
    public static JCFUserService getInstance() {
        // 첫 번째 null 체크 (성능 최적화)
        if (instance == null) {
            synchronized (JCFUserService.class) {
                // 두 번째 null 체크 (동기화 구간 안에서 중복 생성 방지)
                if (instance == null) {
                    instance = new JCFUserService();
                }
            }
        }
        return instance;
    }

    // 필드를 활용해 생성, 조회, 수정, 삭제 메소드 제작
    // 유저 생성(이름, 상태)
    @Override
    public UUID create(String username, String email) {
        User user = new User(username, email);
        data.put(user.getId(), user);
        return user.getId();
    }

    // UUID 기반 유저 조회
    @Override
    public User findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("No data for that ID could be found.: " + id));
    }

    // 모든 유저 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    // UUID를 기반으로 수정
    @Override
    public void update(UUID id, String username, String email) {
        User user = data.get(id);
        if(user == null){
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
        user.updateUser(username, email);
    }

    // 삭제
    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("No data for that ID could be found.: " + id);
        }
        data.remove(id);
    }
}