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

package demo.ddz.poker;

import lombok.Getter;

/** Poker card. */
@Getter
public class Card implements Comparable<Card> {

  private final int id;

  private final CardSuit suit;

  private final CardRank rank;

  private final int value;

  /**
   * Constructor.
   *
   * @param id card id
   */
  public Card(int id) {
    if (id < 1 || id > 54) {
      throw new IllegalArgumentException("Card id should be from 1 to 54");
    }
    this.id = id;
    this.suit = CardSuit.fromValue((id - 1) / 13 + 1);
    if (id == 53) {
      this.rank = CardRank.JOKER_1;
    } else if (id == 54) {
      this.rank = CardRank.JOKER_2;
    } else {
      this.rank = CardRank.fromValue((id - 1) % 13 + 1);
    }
    this.value = rankToValue(rank);
  }

  /**
   * Card rank to value.
   *
   * @param rank {@link CardRank}
   * @return value
   */
  public static int rankToValue(CardRank rank) {
    if (rank == CardRank.ACE) {
      return CardRank.KING.getValue() + 1;
    } else if (rank == CardRank.RANK_2) {
      return CardRank.KING.getValue() + 2;
    } else if (rank == CardRank.JOKER_1) {
      return CardRank.KING.getValue() + 3;
    } else if (rank == CardRank.JOKER_2) {
      return CardRank.KING.getValue() + 4;
    }
    return rank.getValue();
  }

  /**
   * Compare.
   *
   * @param card the object to be compared.
   * @return the value {@code 0} if {@code x == y}; a value less than {@code 0} if {@code x < y};
   *     and a value greater than {@code 0} if {@code x > y}
   */
  @Override
  public int compareTo(Card card) {
    if (this.value == card.value) {
      return Integer.compare(this.id, card.id);
    }
    return Integer.compare(this.value, card.value);
  }
}
