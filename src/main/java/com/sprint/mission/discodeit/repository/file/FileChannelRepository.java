package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.FileRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final File CHANNEL_DIR = new File("output/channeldata");

    public FileChannelRepository() {
        if (CHANNEL_DIR.exists() == false) {
            CHANNEL_DIR.mkdirs();
        }
    }

    // UUID에 대응하는 객체 리턴
    private File getChannelFile(UUID id) {
        return new File(CHANNEL_DIR, id.toString() + ".dat");
    }

    // Channel 객체를 해당 파일에 직렬화하여 저장
    @Override
    public void create(Channel channel) {
        File f = getChannelFile(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 Channel 객체를 역직렬화하여 읽어옴
    @Override
    public Channel findById(UUID id) {
        File f = getChannelFile(id);
        if (f.exists() == false) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Channel> findAll() {
        File[] files = CHANNEL_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            return new ArrayList<>();
        }

        List<Channel> result = new ArrayList<>();
        for (File f : files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Channel channel = (Channel) ois.readObject();
                result.add(channel);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 사용자 정보 업데이트
    @Override
    public void update(UUID id, String newChannelName) {
        Channel channel = findById(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        channel.updateChannel(newChannelName);
        create(channel);
    }

    // 사용자 삭제
    @Override
    public void delete(UUID id) {
        File f = getChannelFile(id);
        if (f.exists() == false || f.isFile() == false) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (deleted == false) {
            throw new RuntimeException("Failed to delete channel file for ID: " + id);
        }
    }
}
