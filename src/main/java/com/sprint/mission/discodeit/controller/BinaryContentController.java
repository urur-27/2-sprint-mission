package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentMapper binaryContentMapper;

  // 단일 파일 조회
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentResponse> findFile(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContentMapper.toResponse(content));
  }

  // 여러 파일 조회
  @GetMapping
  public ResponseEntity<List<BinaryContentResponse>> findFiles(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(binaryContents.stream().map(binaryContentMapper::toResponse).toList());
  }

  // 파일 다운로드
  @GetMapping("{binaryContentId}/download")
  public ResponseEntity<Resource> downloadFile(@PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);

    // 파일 데이터와 정보 구성
    ByteArrayResource resource = new ByteArrayResource(content.getBytes());

    return ResponseEntity.ok()
        .contentLength(content.getSize())
        .contentType(MediaType.parseMediaType(content.getContentType()))
        .header("Content-Disposition", "attachment; filename=\"" + content.getFileName() + "\"")
        .body(resource);
  }
}

