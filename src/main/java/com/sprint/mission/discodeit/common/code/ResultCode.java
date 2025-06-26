package com.sprint.mission.discodeit.common.code;

public enum ResultCode implements Code {

  // 400 Bad Request
  BAD_REQUEST(400, "잘못된 요청입니다."),
  INVALID_CHANNEL_TYPE(400, "유효하지 않은 채널 타입입니다."),
  INVALID_PASSWORD(400, "비밀번호가 올바르지 않습니다."),
  DUPLICATE_EMAIL(400, "이미 등록된 이메일입니다."),
  DUPLICATE_USERNAME(400, "이미 사용 중인 사용자 이름입니다."),
  DUPLICATE_READ_STATUS(400, "이미 읽음 상태가 존재합니다."),
  INVALID_JSON(400, "JSON 형식이 잘못되었습니다."),
  READ_STATUS_ALREADY_EXISTS(400, "이미 읽음 상태가 존재합니다."),
  USER_STATUS_ALREADY_EXISTS(400, "이미 사용자 상태가 존재합니다."),
  INVALID_CHANNEL_DATA(400, "채널명이 입력되지 않았습니다."),

  // 403 Forbidden
  ACCESS_DENIED(403, "권한이 없습니다."),

  // 404 Not Found
  USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
  CHANNEL_NOT_FOUND(404, "채널을 찾을 수 없습니다."),
  MESSAGE_NOT_FOUND(404, "메시지를 찾을 수 없습니다."),
  READ_STATUS_NOT_FOUND(404, "읽음 상태를 찾을 수 없습니다."),
  BINARY_CONTENT_NOT_FOUND(404, "파일을 찾을 수 없습니다."),
  USER_STATUS_NOT_FOUND(404, "사용자 상태를 찾을 수 없습니다."),

  // 500 Internal Server Error
  FILE_PROCESSING_ERROR(500, "파일 처리 중 오류가 발생했습니다."),
  FILE_INITIALIZATION_ERROR(500, "파일 저장소 초기화 중 오류가 발생했습니다."),
  FILE_WRITE_ERROR(500, "파일 저장 중 오류가 발생했습니다."),
  INTERNAL_SERVER_ERROR(500, "서버 내부 오류입니다.");


  private final int code;
  private final String message;

  ResultCode(int code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public int getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
