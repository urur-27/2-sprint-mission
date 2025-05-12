package com.sprint.mission.discodeit.exceptionhandler;

import com.sprint.mission.discodeit.common.code.ResultCode;
import com.sprint.mission.discodeit.exception.RestException;
import com.sprint.mission.discodeit.dto2.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RestException.class)
  public ResponseEntity<ErrorResponse> handleRestException(RestException e,
      HttpServletRequest request) {
    ResultCode resultCode = e.getResultCode();
//    String traceId = request.getHeader("traceId");
    String traceId = MDC.get("traceId");

    // 예외 로그 출력 (개인정보 제외)
    log.error("Handled RestException: code={}, message={}, traceId={}",
        resultCode.getCode(), resultCode.getMessage(), traceId, e);

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.valueOf(resultCode.getCode()),
        resultCode.getMessage(),
        request.getRequestURI()
    );

    return ResponseEntity.status(resultCode.getCode()).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception e,
      HttpServletRequest request) {
//    String traceId = request.getHeader("traceId");
    String traceId = MDC.get("traceId");

    // 예외 로그 출력 (StackTrace 포함)
    log.error("Unhandled Exception: traceId={}, error={}", traceId, e.getMessage(), e);

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "An unexpected error occurred",
        request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
