/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jinganix.ddz.helper.exception;

import io.github.jinganix.ddz.proto.error.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Business exception. */
@Getter
public class ApiException extends RuntimeException {

  private final HttpStatus status;

  private final ErrorCode code;

  private ApiException(HttpStatus httpStatus, ErrorCode errorCode, String message) {
    super(message);
    this.status = httpStatus;
    this.code = errorCode;
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(HttpStatus status, ErrorCode errorCode) {
    return new ApiException(status, errorCode, null);
  }

  /**
   * Create an exception.
   *
   * @param errorCode {@link ErrorCode}
   * @return {@link ApiException}
   */
  public static ApiException of(ErrorCode errorCode) {
    return new ApiException(HttpStatus.BAD_REQUEST, errorCode, null);
  }

  /**
   * Not fill the stacktrace.
   *
   * @return {@link Throwable}
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }
}
