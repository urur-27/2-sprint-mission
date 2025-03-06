package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
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

    // 기본 저장소를 FileUserRepository로 설정
    public static BasicUserService getInstance() {
        return getInstance(new FileUserRepository());
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
    public UUID createUser(String username, String email) {
        User user = new User(username, email);
        userRepository.save(user);
        return user.getId();
    }

    @Override
    public User getUserById(UUID id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NoSuchElementException("No user found for ID: " + id);
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void updateUser(UUID id, String userName, String email) {

        userRepository.update(id, userName, email);
    }

    @Override
    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }

}
