package com.sprint.mission.discodeit.repository;

import java.io.IOException;
import java.nio.file.Path;

public interface FileRepository {
    // 파일과 관련된 기능들을 구현하는 인터페이스
    void createDirectories(Path path) throws IOException; // 파일 저장 경로 생성
    void writeFile(Path path, Object obj) throws IOException; // 파일에 저장
     <T> T readFile(Path path, Class<T> clazz) throws IOException, ClassNotFoundException; // 파일 읽기.
}
