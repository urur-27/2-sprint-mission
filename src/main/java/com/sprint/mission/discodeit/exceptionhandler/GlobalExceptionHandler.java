package com.sprint.mission.discodeit.exceptionhandler;

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


  // 입력된 채널 타입 오류 400
  @ExceptionHandler(InvalidChannelTypeException.class)
  public ResponseEntity<ErrorResponse> handleInvalidType(InvalidChannelTypeException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  // readstatus 중복된 경우 400
  @ExceptionHandler(DuplicateReadStatusException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReadStatus(DuplicateReadStatusException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  // email이 중복된 경우 400
  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReadStatus(DuplicateEmailException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  // username이 중복된 경우 400
  @ExceptionHandler(DuplicateUsernameException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReadStatus(DuplicateUsernameException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  // 패스워드가 일치하지 않는 경우 400
  @ExceptionHandler(InvalidPasswordException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateReadStatus(InvalidPasswordException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  // ReadStatus 없는 경우 404
  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusNotFound(ReadStatusNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // User 없는 경우 404
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // Channel 없는 경우 404
  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // Message 없는 경우 404
  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(MessageNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // BinaryContent 없는 경우 404
  @ExceptionHandler(UserStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserStatusNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // UserStatus 없는 경우 404
  @ExceptionHandler(BinaryContentNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(BinaryContentNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }


  // 파일 탐색 중 오류 500
  @ExceptionHandler(FileProcessingException.class)
  public ResponseEntity<ErrorResponse> handleFileProcessingException(
      FileProcessingException ex,
      HttpServletRequest request
  ) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, request);
  }

  // JSON 입력값이 잘못된 경우, 400
  @ExceptionHandler(InvalidJsonFormatException.class)
  public ResponseEntity<ErrorResponse> handleInvalidJsonFormat(
      InvalidJsonFormatException e,
      HttpServletRequest request
  ) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception e, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e, request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception e,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(status, e.getMessage(),
        request.getRequestURI());
    return ResponseEntity.status(status).body(errorResponse);
  }
}
