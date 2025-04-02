package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto2.*;
import com.sprint.mission.discodeit.dto2.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.dto2.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto2.response.ApiResponse;
import com.sprint.mission.discodeit.dto2.response.UserCreateResponse;
import com.sprint.mission.discodeit.dto2.response.UserResponse;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusRepository userStatusRepository;
    private final AuthService authService;

    // User 등록
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ApiResponse<UserCreateResponse>> createUser(@RequestBody UserCreateRequest request) {
        UUID id = userService.create(request);
        UserCreateResponse response = new UserCreateResponse(
                id,
                request.username(),
                request.email());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User has been registered successfully.", response));
    }

    // User 수정
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ApiResponse<Void>> updateUser(@RequestBody UserUpdateRequest request) {
        userService.update(request);
        return ResponseEntity.ok(new ApiResponse<>("User \"" + request.username() + "\" information has been modified.", null));
    }

    // User 삭제
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id) {
        String userName = userService.findById(id).username();
        userService.delete(id);
        return ResponseEntity.ok(new ApiResponse<>("\"" + userName + "\" has been deleted.", null));
    }

    // 모든 유저 조회
    @RequestMapping(value = "findAll",method = RequestMethod.GET)
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsers() {
        List<UserDto> users = userService.findAll();
        ApiResponse<List<UserDto>> response = new ApiResponse<>("Successfully found all user information", users);
        return ResponseEntity.ok(response);
    }

    // 온라인 상태 갱신
    @RequestMapping(value="/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<ApiResponse<Void>> updateOnlineStatus(@PathVariable UUID id) {
        userStatusRepository.updateLastAccessedAt(id, Instant.now());
        String name = userService.findById(id).username();
        return ResponseEntity.ok(new ApiResponse<>("\"" + name + "\" online status has been updated.", null));
    }

    @RequestMapping(value = "/auth/login", method = RequestMethod.POST)
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest request) {
        UserResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
