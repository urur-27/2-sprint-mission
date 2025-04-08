package com.sprint.mission.discodeit.dto2.request;

public record BinaryContentCreateRequest(
    String fileName,
    String contentType,
    byte[] bytes
) {

}
