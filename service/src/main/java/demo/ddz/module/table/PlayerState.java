/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
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

package demo.ddz.module.table;

import demo.ddz.helper.enumeration.Enumeration;
import demo.ddz.helper.enumeration.IntegerEnumMapper;

public enum PlayerState implements Enumeration<Integer> {

  /** IDLE */
  IDLE(0),

  /** READY */
  READY(1),

  /** BIDDING */
  BIDDING(2),

  /** DOUBLING */
  DOUBLING(3),

  /** PLAYING */
  PLAYING(4),

  /** FOLD */
  FOLD(5),

  /** LOSE */
  LOSE(6);

  private static final IntegerEnumMapper<PlayerState> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  PlayerState(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link PlayerState}
   */
  public static PlayerState fromValue(Integer value) {
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
