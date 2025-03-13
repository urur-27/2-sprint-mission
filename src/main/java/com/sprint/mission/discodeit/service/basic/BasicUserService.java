package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicUserService implements UserService {
    private static volatile BasicUserService instance;
    private final UserRepository userRepository;

    // 생성자를 통해 저장소 주입받기
    private BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 다른 저장소를 주입 받을 수 있도록 getInstance 오버로딩
    public static BasicUserService getInstance(UserRepository userRepository) {
        if (instance == null) {
            synchronized (BasicUserService.class) {
                if (instance == null) {
                    instance = new BasicUserService(userRepository);
                }
            }
        }
        return instance;
    }

    @Override
    public UUID create(String username, String email) {
        User user = new User(username, email);
        userRepository.upsert(user);
        return user.getId();
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("No user found for ID: " + id);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(UUID id, String userName, String email) {

        userRepository.update(id, userName, email);
    }

    @Override
    public void delete(UUID id) {
        userRepository.delete(id);
    }

}
