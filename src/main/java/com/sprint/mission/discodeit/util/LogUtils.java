package com.sprint.mission.discodeit.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtils {

  private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);

  private static boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  public static String mask(String input) {
    if (isDebugEnabled() || input == null || input.length() < 3) {
      return input;
    }
    return input.substring(0, 1) + "***" + input.substring(input.length() - 1);
  }

  public static String maskUUID(UUID uuid) {
    if (isDebugEnabled() || uuid == null) {
      return uuid != null ? uuid.toString() : "***";
    }
    String uuidString = uuid.toString();
    return uuidString.substring(0, 4) + "****" + uuidString.substring(uuidString.length() - 4);
  }

  public static String maskUUIDList(List<UUID> uuids) {
    if (uuids == null || uuids.isEmpty()) {
      return "[]";
    }
    return uuids.stream()
        .map(LogUtils::maskUUID)
        .collect(Collectors.joining(", ", "[", "]"));
  }

  public static String maskEmail(String email) {
    if (isDebugEnabled() || email == null || email.length() < 5 || !email.contains("@")) {
      return email;
    }
    String[] parts = email.split("@");
    return mask(parts[0]) + "@" + parts[1];
  }

  public static String maskFileName(String fileName) {
    if (isDebugEnabled() || fileName == null || fileName.length() < 3) {
      return fileName;
    }
    int dotIndex = fileName.lastIndexOf(".");
    if (dotIndex > 0) {
      String name = fileName.substring(0, dotIndex);
      String extension = fileName.substring(dotIndex);
      return mask(name) + extension;
    }
    return mask(fileName);
  }
}
