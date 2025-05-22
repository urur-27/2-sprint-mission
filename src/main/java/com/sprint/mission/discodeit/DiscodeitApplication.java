package com.sprint.mission.discodeit;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {
  public static void main(String[] args) {
    Dotenv dotenv = Dotenv.load();

    // 환경 변수 등록
    dotenv.entries().forEach(entry ->
        System.setProperty(entry.getKey(), entry.getValue())
    );

    SpringApplication.run(DiscodeitApplication.class, args);
  }
}
