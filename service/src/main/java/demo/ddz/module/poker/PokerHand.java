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

package demo.ddz.module.poker;

import demo.ddz.helper.enumeration.Enumeration;
import demo.ddz.helper.enumeration.IntegerEnumMapper;

/** Poker hand. */
public enum PokerHand implements Enumeration<Integer> {
  /** Single card, eg: 3. */
  SINGLE(1),

  /** Two same cards, eg: 33. */
  PAIR(2),

  /** Three same cards, eg: 333. */
  THREE_OF_KIND(3),

  /** Three same cards and one other card, eg: 3335. */
  THREE_WITH_SINGLE(4),

  /** Three same cards and two other same cards, eg: 33344. */
  THREE_WITH_PAIR(5),

  /** Five or more consecutive cards, excludes (2, E, F), eg: 34567. */
  STRAIGHT(6),

  /** Three or more consecutive pair cards, excludes 2, eg: 334455. */
  DOUBLE_STRAIGHT(7),

  /** Two or more consecutive triple cards, excludes 2, eg: 333444. */
  TRIPLE_STRAIGHT(8),

  /** One or more consecutive triple cards and a single card for each, excludes 2, eg: 33344468. */
  TRIPLE_STRAIGHT_WITH_SINGLES(9),

  /** One or more consecutive triple cards and a cards pair for each, excludes 2, eg: 3334446688. */
  TRIPLE_STRAIGHT_WITH_PAIRS(10),

  /** Four same cards and two other different cards, eg: 333357. */
  FOUR_WITH_TWO(11),

  /** Four same cards and two other same cards, eg: 333355. */
  FOUR_WITH_PAIR(12),

  /** Four same cards and two pair of other same cards, eg: 33335577. */
  FOUR_WITH_TWO_PAIRS(13),

  /** Four same cards. */
  FOUR_OF_KIND(14),

  /** Two jokers. */
  ROCKET(15);

  private static final IntegerEnumMapper<PokerHand> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  PokerHand(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link PokerHand}
   */
  public static PokerHand fromValue(Integer value) {
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
