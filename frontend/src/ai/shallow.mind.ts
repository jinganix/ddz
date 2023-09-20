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

import { find, flatten, groupBy, some } from "lodash";
import { Card } from "@/poker/card";
import { CardRank } from "@/poker/card.rank";
import { CardsKind } from "@/ai/cards.kind";
import { CardsSet } from "@/poker/cards.set";
import { PokerHand } from "@/poker/poker.hand";

export class ShallowMind {
  private static readonly VALUE_OF_RANK_2 = Card.rankToValue(CardRank.RANK_2);

  private static readonly VALUE_OF_JOKER_1 = Card.rankToValue(CardRank.JOKER_1);

  private static readonly VALUE_OF_JOKER_2 = Card.rankToValue(CardRank.JOKER_2);

  kinds: CardsKind[] = [];

  constructor(cards: Card[]) {
    const cardsMap = groupBy(cards, (e) => e.value);
    for (const key in cardsMap) {
      this.kinds.push(new CardsKind(Number(key), cardsMap[key]));
    }
    this.kinds.sort((a, b) => a.value - b.value);
  }

  leadOff(): Card[] {
    const handCards = flatten(this.kinds.map((e) => e.cards));
    if (new CardsSet(handCards).pokerHand != null) {
      return handCards;
    }
    let cards = this.popThreeWith();
    if (cards.length) {
      return cards;
    }
    cards = this.popMaxStraight(0, 1, 5);
    if (cards.length) {
      return cards;
    }
    cards = this.popMaxStraight(0, 2, 3);
    if (cards.length) {
      return cards;
    }
    cards = this.popKind(0, 1, 1);
    if (cards.length) {
      return cards;
    }
    return this.popKind(0, 2, 2);
  }

  private popThreeWith(): Card[] {
    const cards = this.popKind(0, 3, 3);
    if (!cards.length) {
      return cards;
    }
    let popped = this.popKind(0, 1, 1);
    if (popped.length) {
      cards.push(...popped);
      return cards;
    }
    popped = this.popKind(0, 2, 2);
    cards.push(...popped);
    return cards;
  }

  followSuit(cardsSet: CardsSet): Card[] {
    return this.popCards(cardsSet.pokerHand!, cardsSet.value, cardsSet.cards.length);
  }

  private popCards(pokerHand: PokerHand, minValue: number, cardSize: number): Card[] {
    const cards: Card[] = this.matchPokerHand(pokerHand, minValue, cardSize);
    if (
      cards.length !== 0 ||
      pokerHand === PokerHand.FOUR_OF_KIND ||
      pokerHand === PokerHand.ROCKET
    ) {
      return cards;
    }

    return this.mayPopBomb();
  }

  private matchPokerHand(pokerHand: PokerHand, minValue: number, cardSize: number): Card[] {
    switch (pokerHand) {
      case PokerHand.SINGLE:
        return this.popKind(minValue, 1, 3);
      case PokerHand.PAIR:
        return this.popKind(minValue, 2, 3);
      case PokerHand.THREE_OF_KIND:
        return this.popKind(minValue, 3, 3);
      case PokerHand.THREE_WITH_SINGLE: {
        const cards: Card[] = this.popKind(minValue, 3, 3);
        if (cards.length === 0) {
          return cards;
        }
        return this.popOrRevert(cards, 1);
      }
      case PokerHand.THREE_WITH_PAIR: {
        const cards: Card[] = this.popKind(minValue, 3, 3);
        if (cards.length === 0) {
          return cards;
        }
        return this.popOrRevert(cards, 2);
      }
      case PokerHand.STRAIGHT:
        return this.popStraight(minValue, 1, cardSize);
      case PokerHand.DOUBLE_STRAIGHT:
        return this.popStraight(minValue, 2, cardSize / 2);
      case PokerHand.TRIPLE_STRAIGHT:
        return this.popStraight(minValue, 3, cardSize / 3);
      case PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES: {
        const len: number = cardSize / 4;
        let cards: Card[] = this.popStraight(minValue, 3, len);
        if (cards.length === 0) {
          return cards;
        }
        for (let i = 0; i < len; i++) {
          cards = this.popOrRevert(cards, 1);
          if (cards.length === 0) {
            return cards;
          }
        }
        return cards;
      }
      case PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS: {
        const len: number = cardSize / 5;
        let cards: Card[] = this.popStraight(minValue, 3, len);
        if (cards.length === 0) {
          return cards;
        }
        for (let i = 0; i < len; i++) {
          cards = this.popOrRevert(cards, 2);
          if (cards.length === 0) {
            return cards;
          }
        }
        return cards;
      }
      case PokerHand.FOUR_WITH_TWO: {
        const cards: Card[] = this.popKind(minValue, 4, 4);
        if (cards.length === 0) {
          return this.popBomb();
        }
        let singles: Card[] = [];
        for (let i = 0; i < 2; i++) {
          singles = this.popOrRevert(singles, 1);
          if (singles.length === 0) {
            return cards;
          }
        }
        return cards.concat(singles);
      }
      case PokerHand.FOUR_WITH_PAIR: {
        const cards: Card[] = this.popKind(minValue, 4, 4);
        if (cards.length === 0) {
          return this.popBomb();
        }
        const pair: Card[] = this.popKind(0, 2, 3);
        if (pair.length !== 0) {
          return cards.concat(pair);
        } else {
          return cards;
        }
      }
      case PokerHand.FOUR_WITH_TWO_PAIRS: {
        const cards: Card[] = this.popKind(minValue, 4, 4);
        if (cards.length === 0) {
          return this.popBomb();
        }
        let pairs: Card[] = [];
        for (let i = 0; i < 2; i++) {
          pairs = this.popOrRevert(pairs, 2);
          if (pairs.length === 0) {
            return cards;
          }
        }
        return cards.concat(pairs);
      }
      case PokerHand.FOUR_OF_KIND: {
        const cards: Card[] = this.popKind(minValue, 4, 4);
        return cards.length === 0 ? this.popRocket() : cards;
      }
      default:
        return [];
    }
  }

  private hasKind(value: number): boolean {
    return some(this.kinds, (e) => e.value == value);
  }

  private getKind(value: number): CardsKind | null {
    return find(this.kinds, (e) => e.value === value) || null;
  }

  private mayPopBomb(): Card[] {
    const hasRocket: boolean = this.hasRocket();
    const hasBomb: boolean = some(this.kinds, (kind) => kind.size() === 4);
    if (!hasRocket && !hasBomb) {
      return [];
    }
    const remaining: Card[] = [];
    for (const kind of this.kinds) {
      if (
        kind.size() !== 4 &&
        kind.value !== ShallowMind.VALUE_OF_JOKER_1 &&
        kind.value !== ShallowMind.VALUE_OF_JOKER_2
      ) {
        remaining.push(...kind.cards);
      }
    }
    if (remaining.length !== 0 && new CardsSet(remaining).pokerHand === null) {
      return [];
    }
    return hasRocket ? this.popRocket() : this.popKind(0, 4, 4);
  }

  private popBomb(): Card[] {
    return this.hasRocket() ? this.popRocket() : this.popKind(0, 4, 4);
  }

  private hasRocket(): boolean {
    return this.hasKind(ShallowMind.VALUE_OF_JOKER_1) && this.hasKind(ShallowMind.VALUE_OF_JOKER_2);
  }

  private popRocket(): Card[] {
    if (this.kinds.length < 2) {
      return [];
    }

    if (!this.hasRocket()) {
      return [];
    }

    const cards: Card[] = [];
    cards.push(...this.getKind(ShallowMind.VALUE_OF_JOKER_1)!.pop(1));
    cards.push(...this.getKind(ShallowMind.VALUE_OF_JOKER_2)!.pop(1));

    return cards;
  }

  private popOrRevert(cards: Card[], n: number): Card[] {
    const popped: Card[] = this.popKind(0, n, 3);

    if (popped.length !== 0) {
      cards.push(...popped);
      return cards;
    }

    for (const card of cards) {
      this.getKind(card.value)!.push(card);
    }

    return [];
  }

  private popKind(minValue: number, n: number, maxN: number): Card[] {
    const indices: (CardsKind | null)[] = [null, null, null, null];

    for (const kind of this.kinds) {
      if (kind.value <= minValue || kind.size() < n || kind.size() > maxN) {
        continue;
      }
      if (kind.size() === n) {
        if (
          kind.value === ShallowMind.VALUE_OF_JOKER_1 &&
          this.hasKind(ShallowMind.VALUE_OF_JOKER_2)
        ) {
          break;
        }
        return kind.pop(n);
      }
      if (indices[kind.size() - 1] === null) {
        indices[kind.size() - 1] = kind;
      }
    }

    for (let i = n; i < indices.length; i++) {
      if (indices[i] !== null) {
        return indices[i]!.pop(n);
      }
    }
    return [];
  }

  private popMaxStraight(minValue: number, n: number, minLen: number): Card[] {
    let fromValue: number = minValue + 1;
    for (let value = fromValue; value < ShallowMind.VALUE_OF_RANK_2; value++) {
      const kind = this.getKind(value);
      if (kind === null || kind.size() < n) {
        if (value - fromValue >= minLen) {
          const values: Card[] = [];
          for (let i = fromValue; i < value; i++) {
            values.push(...this.getKind(i)!.pop(n));
          }
          return values;
        }
        fromValue = value + 1;
      }
    }
    return [];
  }

  private popStraight(minValue: number, n: number, len: number): Card[] {
    let fromValue: number = minValue + 1;
    for (let value = fromValue; value < ShallowMind.VALUE_OF_RANK_2; value++) {
      const kind = this.getKind(value);
      if (kind === null || kind.size() < n) {
        fromValue = value + 1;
        continue;
      }
      if (value - fromValue + 1 < len) {
        continue;
      }
      const values: Card[] = [];
      for (let i = fromValue; i <= value; i++) {
        values.push(...this.getKind(i)!.pop(n));
      }
      return values;
    }
    return [];
  }
}
