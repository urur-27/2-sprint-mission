package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> data = new HashMap<>();

  @Override
  public User upsert(User user) {
    data.put(user.getId(), user);
    return user;
  }

  @Override
  public User findById(UUID id) {
    return data.get(id);
  }

  @Override
  public User findByUsername(String username) {
    return this.findAll().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void update(UUID id, String newUsername, String newEmail, String newPassword,
      UUID profileId) {
    User user = data.get(id);
    if (user != null) {
      user.updateUser(newUsername, newEmail, newPassword, profileId);
    }
  }

  @Override
  public void delete(UUID id) {
    data.remove(id);
  }

  @Override
  public boolean existsByEmail(String email) {
    return this.findAll().stream().anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public boolean existsByUsername(String username) {
    return this.findAll().stream().anyMatch(user -> user.getUsername().equals(username));
  }
}