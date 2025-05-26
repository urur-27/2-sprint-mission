package com.sprint.mission.discodeit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// 환경변수 적용이 정상적으로 되었는지 테스트
@SpringBootTest
public class EnvPropertyTest {
  static {
    // 테스트 실행 전에 .env를 수동으로 읽어 시스템에 주입
    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );
  }

  @Value("${discodeit.storage.type}")
  private String storageType;

  @Value("${discodeit.storage.local.root-path:}")
  private String localRootPath;

  @Value("${discodeit.storage.s3.access-key:}")
  private String s3AccessKey;

  @Value("${discodeit.storage.s3.region:}")
  private String s3Region;

  @Test
  @DisplayName("환경 변수 및 설정 파일에서 값을 제대로 불러오는지 테스트")
  void envPropertiesShouldBeLoadedCorrectly() {
    System.out.println("📦 storageType = " + storageType);
    System.out.println("📦 localRootPath = " + localRootPath);
    System.out.println("📦 s3AccessKey = " + s3AccessKey);
    System.out.println("📦 s3Region = " + s3Region);

    assertThat(storageType).isNotBlank();

    if (storageType.equals("local")) {
      assertThat(localRootPath).isNotBlank();
    }

    if (storageType.equals("s3")) {
      assertThat(s3AccessKey).isNotBlank();
      assertThat(s3Region).isNotBlank();
    }
  }
}
