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

package demo.ddz.module.phase;

import demo.ddz.helper.enumeration.IntegerEnumMapper;
import demo.ddz.helper.phase.PhaseType;

public enum DdzPhaseType implements PhaseType {

  /** IDLE */
  IDLE(0),

  /** COUNTDOWN */
  COUNTDOWN(1),

  /** DEALING */
  DEALING(2),

  /** BIDDING */
  BIDDING(3),

  /** DOUBLING */
  DOUBLING(4),

  /** PLAYING */
  PLAYING(5),

  /** SETTLEMENT */
  SETTLEMENT(6),

  /** END */
  END(7);

  private static final IntegerEnumMapper<DdzPhaseType> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  DdzPhaseType(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link DdzPhaseType}
   */
  public static DdzPhaseType fromValue(Integer value) {
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
