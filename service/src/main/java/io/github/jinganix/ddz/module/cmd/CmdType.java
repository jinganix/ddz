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

package io.github.jinganix.ddz.module.cmd;

import io.github.jinganix.ddz.helper.enumeration.Enumeration;
import io.github.jinganix.ddz.helper.enumeration.IntegerEnumMapper;

public enum CmdType implements Enumeration<Integer> {
  reserved(0),
  tableBid(1),
  tableDouble(2),
  tableFold(3),
  tablePlay(4);

  private static final IntegerEnumMapper<CmdType> mapper = new IntegerEnumMapper<>(values());

  private final int value;

  CmdType(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link CmdType}
   */
  public static CmdType fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get enumeration value.
   *
   * @return {@link Integer}
   */
  @Override
  public Integer getValue() {
    return this.value;
  }
}
