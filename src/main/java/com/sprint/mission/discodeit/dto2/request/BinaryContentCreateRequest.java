package com.sprint.mission.discodeit.dto2.request;

public record BinaryContentCreateRequest(
   byte[] data,
   String contentType,
   long size
) {}
