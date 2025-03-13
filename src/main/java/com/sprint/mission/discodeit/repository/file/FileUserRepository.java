package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static final File USER_DIR = new File("output/userdata");

    public FileUserRepository() {
        if (USER_DIR.exists() == false) {
            USER_DIR.mkdirs();
        }
    }

    // UUID에 대응하는 객체 리턴
    private File getUserFile(UUID id) {
        return new File(USER_DIR, id.toString() + ".dat");
    }

    // User 객체를 해당 파일에 직렬화하여 저장
    @Override
    public void create(User user) {
        File f = getUserFile(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 User 객체를 역직렬화하여 읽어옴
    @Override
    public User findById(UUID id) {
        File f = getUserFile(id);
        if (!f.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> findAll() {
        File[] files = USER_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<User> result = new ArrayList<>();
        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                User user = (User) ois.readObject();
                result.add(user);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 사용자 정보 업데이트
    @Override
    public void update(UUID id, String newUsername, String newEmail) {
        User user = findById(id);
        if (user == null) {
            throw new NoSuchElementException("No user file found for ID: " + id);
        }
        user.updateUser(newUsername, newEmail);
        create(user);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        File f = getUserFile(id);
        if (f.exists() == false || f.isFile() == false) {
            throw new NoSuchElementException("No user file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (deleted == false) {
            throw new RuntimeException("Failed to delete user file for ID: " + id);
        }
    }

}
