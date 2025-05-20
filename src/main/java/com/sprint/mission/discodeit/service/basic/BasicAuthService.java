package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.dto2.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.util.LogUtils;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserStatusRepository userStatusRepository;

  @Override
  @Transactional(readOnly = true)
  public User login(UserLoginRequest loginRequest) {
    String traceId = MDC.get("traceId");
    String username = loginRequest.username();
    String password = loginRequest.password();

    // 시작 로그
    log.info("[LOGIN] status=START, username={}, traceId={}",
        log.isDebugEnabled() ? username : LogUtils.mask(username), traceId);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.warn("[LOGIN] User not found: username={}, traceId={}",
              LogUtils.mask(username), traceId);
          return new RestException(ResultCode.USER_NOT_FOUND);
        });

    if (!user.getPassword().equals(password)) {
      log.warn("[LOGIN] Invalid password for user: username={}, traceId={}",
          LogUtils.mask(username), traceId);
      throw new RestException(ResultCode.INVALID_PASSWORD);
    }

    // 로그인 시 상태 갱신
    if (user.getStatus() == null) {
      userStatusRepository.save(new UserStatus(user, Instant.now()));
      log.info("[LOGIN] User status created: userId={}, traceId={}",
          LogUtils.maskUUID(user.getId()), traceId);
    } else {
      user.getStatus().setOnline();
      log.info("[LOGIN] User set to online: userId={}, traceId={}",
          LogUtils.maskUUID(user.getId()), traceId);
    }

    // 성공 로그
    log.info("[LOGIN] status=SUCCESS, userId={}, traceId={}",
        LogUtils.maskUUID(user.getId()), traceId);
    return user;
  }

}