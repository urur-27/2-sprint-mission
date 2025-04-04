package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.response.FileMetaResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  // 단일 파일 조회
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findFile(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(content);
  }

  // 여러 파일 조회
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findFiles(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents);
  }
}
