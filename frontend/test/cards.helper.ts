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

import { CardRank } from "@/poker/card.rank";
import { Card } from "@/poker/card";

export class CardsHelper {
  private static readonly RANK_MAP = {
    "0": CardRank.RANK_10,
    "2": CardRank.RANK_2,
    "3": CardRank.RANK_3,
    "4": CardRank.RANK_4,
    "5": CardRank.RANK_5,
    "6": CardRank.RANK_6,
    "7": CardRank.RANK_7,
    "8": CardRank.RANK_8,
    "9": CardRank.RANK_9,
    A: CardRank.ACE,
    D: CardRank.JOKER_2,
    J: CardRank.JACK,
    K: CardRank.KING,
    Q: CardRank.QUEEN,
    X: CardRank.JOKER_1,
  } as Record<string, CardRank>;

  private cardsMap = {} as Record<CardRank, Card[]>;

  constructor(cards?: Card[]) {
    if (cards) {
      for (const card of cards) {
        this.cardsMap[card.rank] || (this.cardsMap[card.rank] = []);
        this.cardsMap[card.rank].push(card);
      }
    }
  }

  initialize(): void {
    Object.keys(CardRank)
      .filter((e) => !isNaN(Number(e)))
      .forEach((k) => (this.cardsMap[k as unknown as CardRank] = []));
    for (let id = 1; id <= 54; id++) {
      const card = new Card(id);
      this.cardsMap[card.rank].push(card);
    }
  }

  toCards(str: string): Card[] {
    const parts = str.split("");
    const cards: Card[] = [];
    for (const part of parts) {
      const rank: CardRank = CardsHelper.RANK_MAP[part];
      const cardsOfRank = this.cardsMap[rank];
      cards.push(cardsOfRank.pop()!);
    }
    return cards;
  }
}
