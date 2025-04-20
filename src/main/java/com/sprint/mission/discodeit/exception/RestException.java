package com.sprint.mission.discodeit.exception;

import com.sprint.mission.discodeit.common.code.ResultCode;

public class RestException extends RuntimeException {

  private final ResultCode resultCode;

  public RestException(ResultCode resultCode) {
    super(resultCode.getMessage());
    this.resultCode = resultCode;
  }

  public ResultCode getResultCode() {
    return resultCode;
  }

  public int getCode() {
    return resultCode.getCode();
  }

  @Override
  public String getMessage() {
    return resultCode.getMessage();
  }
}
