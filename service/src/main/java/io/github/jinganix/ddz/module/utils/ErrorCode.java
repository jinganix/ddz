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

package io.github.jinganix.ddz.module.utils;

import io.github.jinganix.ddz.helper.enumeration.Enumeration;
import io.github.jinganix.ddz.helper.enumeration.IntegerEnumMapper;
import io.github.jinganix.ddz.helper.exception.BusinessErrorCode;

public enum ErrorCode implements BusinessErrorCode, Enumeration<Integer> {
  OK(0),
  TABLE_NOT_FOUND(1),
  PHASE_INVALID(2),
  NOT_CURRENT_PLAYER(3),
  INVALID_PLAYED_CARDS(4),
  PLAYED_CARDS_NOT_DOMINATING(5),
  INVALID_PLAYER_STATE(6),
  PLAYER_IS_OFFLINE(7);

  private static final IntegerEnumMapper<ErrorCode> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  ErrorCode(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link ErrorCode}
   */
  public static ErrorCode fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get enumeration value.
   *
   * @return {@link Integer}
   */
  @Override
  public Integer getValue() {
    return value;
  }
}
