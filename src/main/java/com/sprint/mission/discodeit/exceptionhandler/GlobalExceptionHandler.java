package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.exception.notfound.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.duplicate.DuplicateUsernameException;
import com.sprint.mission.discodeit.dto2.response.ErrorResponse;
import com.sprint.mission.discodeit.exception.FileProcessingException;
import com.sprint.mission.discodeit.exception.invalid.InvalidChannelTypeException;
import com.sprint.mission.discodeit.exception.invalid.InvalidJsonFormatException;
import com.sprint.mission.discodeit.exception.invalid.InvalidPasswordException;
import com.sprint.mission.discodeit.exception.notfound.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserNotFoundException;
import com.sprint.mission.discodeit.exception.notfound.UserStatusNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RestException.class)
  public ResponseEntity<ErrorResponse> handleRestException(RestException e,
      HttpServletRequest request) {
    ResultCode resultCode = e.getResultCode();

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.valueOf(resultCode.getCode()),
        resultCode.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(resultCode.getCode()).body(errorResponse);
  }
}
