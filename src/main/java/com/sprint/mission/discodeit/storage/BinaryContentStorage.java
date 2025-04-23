package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;


public interface BinaryContentStorage {

  // BinaryContent의 Id 정보를 바탕으로 byte 데이터 저장
  UUID put(UUID uuid, byte[] bytes);

  // BinaryContent의 Id 정보를 바탕으로 byte[] 데이터를 읽어 InputStream 타입으로 반환
  InputStream get(UUID uuid);

  // BinaryContentResponse 정보를 바탕으로 파일을 다운로드할 수 있는 응답을 반환
  ResponseEntity<?> download(BinaryContentResponse binaryContentResponse);

  void delete(UUID uuid);
}
