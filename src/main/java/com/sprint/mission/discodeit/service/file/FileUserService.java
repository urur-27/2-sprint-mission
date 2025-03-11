package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;


// 각 User를 개별 파일로 직렬화/역직렬화하는 서비스 구현체
public class FileUserService implements UserService {
    // users를 저장할 디렉토리(상대 경로)
    private static final File USER_DIR = new File("output/userdata");

    // 싱글턴 패턴
    private static volatile FileUserService instance;

    private FileUserService() {
        // 생성자에서 디렉토리가 없는 경우 생성
        if (!USER_DIR.exists()) {
            USER_DIR.mkdirs();
        }
    }

    public static FileUserService getInstance() {
        if (instance == null) {
            synchronized (FileUserService.class) {
                if (instance == null) {
                    instance = new FileUserService();
                }
            }
        }
        return instance;
    }

    // 특정 UUID에 대응하는 파일 객체 리턴.
    // 예: output/user_data/123e4567-e89b-12d3-a456-426614174000.dat
    private File getUserFile(UUID id) {
        return new File(USER_DIR, id.toString() + ".dat");
    }

    // User 객체를 해당 파일(output/user_data/{UUID}.dat)에 직렬화하여 저장
    private void saveUserToFile(User user) {
        // user.getId()를 통하여 파일명 가져오기
        File f = getUserFile(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 User 객체를 역직렬화하여 읽어옴
    private User loadUserFromFile(UUID id) {
        File f = getUserFile(id);
        if (!f.exists()) {
            // 해당 id의 파일이 없는 경우 return null
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            // User로 캐스팅하여 리턴
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------
    //  CRUD 구현
    // -----------------------

    @Override
    public UUID create(String username, String email) {
        User user = new User(username, email);
        // User를 개별 파일에 직렬화 저장
        saveUserToFile(user);
        return user.getId();
    }

    @Override
    public User findById(UUID id) {
        return Optional.ofNullable(loadUserFromFile(id))
                .orElseThrow(() -> new NoSuchElementException("No data for that ID could be found.: " + id));
    }

    @Override
    public List<User> findAll() {
        File[] files = USER_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            // 폴더가 존재하지 않거나 IO 에러 등
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

    @Override
    public void update(UUID id, String username, String email) {
        User user = loadUserFromFile(id);
        if (user == null) {
            throw new NoSuchElementException("No user file found for ID: " + id);
        }
        user.updateUser(username, email);
        // 수정 후 다시 저장
        saveUserToFile(user);
    }

    @Override
    public void delete(UUID id) {
        File f = getUserFile(id);
        if (!f.exists() || !f.isFile()) {
            throw new NoSuchElementException("No user file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (!deleted) {
            throw new RuntimeException("Failed to delete user file for ID: " + id);
        }
    }
}
