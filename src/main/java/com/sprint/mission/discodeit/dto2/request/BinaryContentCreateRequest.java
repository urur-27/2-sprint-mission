package com.sprint.mission.discodeit.dto2.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "File name is required")
    @Size(max = 255, message = "Please enter a file name of 255 characters or less.")
    String fileName,

    @NotBlank(message = "Content type is required.")
    @Size(max = 100, message = "Please enter content type within 100 characters.")
    String contentType,

    @NotNull(message = "File byte data is a required field.")
    @Size(min = 1, message = "The file cannot contain empty data.")
    byte[] bytes
) {

}
