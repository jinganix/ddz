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

import { CardSuit } from "@/poker/card.suit";
import { CardRank } from "@/poker/card.rank";

export class Card {
  readonly id: number;

  readonly suit: CardSuit;

  readonly rank: CardRank;

  readonly value: number;

  constructor(id: number) {
    if (id < 1 || id > 54) {
      throw new Error("Card id should be from 1 to 54");
    }
    this.id = id;
    this.suit = Math.floor((id - 1) / 13) + 1;
    if (id === 53) {
      this.rank = CardRank.JOKER_1;
    } else if (id === 54) {
      this.rank = CardRank.JOKER_2;
    } else {
      this.rank = ((id - 1) % 13) + 1;
    }
    this.value = Card.rankToValue(this.rank);
  }

  static rankToValue(rank: CardRank): number {
    if (rank === CardRank.ACE) {
      return CardRank.KING + 1;
    } else if (rank === CardRank.RANK_2) {
      return CardRank.KING + 2;
    } else if (rank === CardRank.JOKER_1) {
      return CardRank.KING + 3;
    } else if (rank === CardRank.JOKER_2) {
      return CardRank.KING + 4;
    }
    return rank;
  }

  compareTo(card: Card): number {
    if (this.value === card.value) {
      return this.id === card.id ? 0 : this.id - card.id > 0 ? 1 : -1;
    }
    return this.value - card.value > 0 ? 1 : -1;
  }
}
