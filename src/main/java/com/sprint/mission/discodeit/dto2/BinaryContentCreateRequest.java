package com.sprint.mission.discodeit.dto2;

public record BinaryContentCreateRequest(
   byte[] data,
   String contentType,
   long size
) {}
