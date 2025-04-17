package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  // 저장 로직을 위한 인터페이스
  User upsert(User user);  // 저장 (JCF: 메모리(Map)에 저장, File: 파일로 저장)

  User findById(UUID id);  // ID를 기반으로 찾기

  User findByUsername(String username);

  List<User> findAll();  // 모든 데이터 찾기

//  void update(UUID id, String newUsername, String newEmail, String newPassword, UUID profileId);

  void delete(UUID id);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);
}