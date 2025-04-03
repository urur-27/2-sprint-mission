package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicateReadStatusException;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.exception.FileProcessingException;
import com.sprint.mission.discodeit.exception.InvalidChannelTypeException;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

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

  // ReadStatus 없는 경우 404
  @ExceptionHandler(ReadStatusNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleReadStatusNotFound(ReadStatusNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // UserId 없는 경우 404
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // 채널이 없는 경우 404
  @ExceptionHandler(ChannelNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleChannelNotFound(ChannelNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // MessageId 없는 경우 404
  @ExceptionHandler(MessageNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(MessageNotFoundException e,
      HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, e, request);
  }

  // 파일 탐색 중 오류 500
  @ExceptionHandler(FileProcessingException.class)
  public ResponseEntity<Map<String, String>> handleFileProcessingException(
      FileProcessingException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", "File processing error");
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
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
