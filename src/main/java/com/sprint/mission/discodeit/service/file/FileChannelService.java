package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

// Channel을 개별 파일로 직렬화/역직렬화하는 서비스 구현체
public class FileChannelService implements ChannelService {
    // channel를 저장할 디렉토리(상대 경로)
    private static final File CHANNEL_DIR = new File("output/channeldata");

    // 싱글턴 패턴
    private static volatile FileChannelService instance;

    private FileChannelService() {
        // 생성자에서 디렉토리가 없는 경우 생성
        if (!CHANNEL_DIR.exists()) {
            CHANNEL_DIR.mkdirs();
        }
    }

    public static FileChannelService getInstance() {
        if (instance == null) {
            synchronized (FileChannelService.class) {
                if (instance == null) {
                    instance = new FileChannelService();
                }
            }
        }
        return instance;
    }

    // 특정 UUID에 대응하는 파일 객체 리턴.
    // 예: output/channel_data/123e4567-e89b-12d3-a456-426614174000.dat
    private File getChannelFile(UUID id) {
        return new File(CHANNEL_DIR, id.toString() + ".dat");
    }

    // Channel 객체를 해당 파일(output/channel_data/{UUID}.dat)에 직렬화하여 저장
    private void saveChannelToFile(Channel channel) {
        // channel.getId()를 통하여 파일명 가져오기
        File f = getChannelFile(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 Channel 객체를 역직렬화하여 읽어옴
    private Channel loadChannelFromFile(UUID id) {
        File f = getChannelFile(id);
        if (!f.exists()) {
            // 해당 id의 파일이 없는 경우 return null
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            // Channel로 캐스팅하여 리턴
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------
    //  CRUD 구현
    // -----------------------

    @Override
    public UUID createChannel(String channelname) {
        Channel channel = new Channel(channelname);
        // Channel를 개별 파일에 직렬화 저장
        saveChannelToFile(channel);
        return channel.getId();
    }


    @Override
    public Channel getChannelById(UUID id) {
        return Optional.ofNullable(loadChannelFromFile(id))
                .orElseThrow(() -> new NoSuchElementException("No data for that ID could be found.: " + id));

//        Channel channel = loadChannelFromFile(id);
//        if (channel == null) {
//            // 해당 id의 Channel 파일이 없다면 예외처리
//            throw new NoSuchElementException("No channel file found for ID: " + id);
//        }
//        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        File[] files = CHANNEL_DIR.listFiles((dir, name) -> name.endsWith(".dat"));
        if (files == null) {
            // 폴더가 존재하지 않거나 IO 에러 등
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

    @Override
    public void updateChannel(UUID id, String channelname) {
        Channel channel = loadChannelFromFile(id);
        if (channel == null) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        channel.updateChannel(channelname);
        // 수정 후 다시 저장
        saveChannelToFile(channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        File f = getChannelFile(id);
        if (!f.exists() || !f.isFile()) {
            throw new NoSuchElementException("No channel file found for ID: " + id);
        }
        boolean deleted = f.delete();
        if (!deleted) {
            throw new RuntimeException("Failed to delete channel file for ID: " + id);
        }
    }
}
