package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.response.FileMetaResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 단일 파일 조회
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<Resource> findFile(@RequestParam UUID binaryContentId) {
        BinaryContent content = binaryContentService.findById(binaryContentId);
        ByteArrayResource resource = new ByteArrayResource(content.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content.getContentType()))
                .contentLength(content.getSize())
                .body(resource);
    }

    // 여러 파일 조회
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<List<FileMetaResponse>>> findFiles(@RequestParam List<UUID> ids) {
        List<BinaryContent> contents = binaryContentService.findAllByIdIn(ids);

        List<FileMetaResponse> metas = contents.stream()
                .map(content -> new FileMetaResponse(
                        content.getId().toString(),
                        content.getContentType(),
                        "/api/binaryContent/view?binaryContentId=" + content.getId()
                ))
                .collect(Collectors.toList());

        ApiResponse<List<FileMetaResponse>> response =
                new ApiResponse<>("Successfully fetched file metadata list.", metas);

        return ResponseEntity.ok(response);
    }
}
