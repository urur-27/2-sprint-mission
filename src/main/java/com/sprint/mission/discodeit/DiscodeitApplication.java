package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto2.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}
