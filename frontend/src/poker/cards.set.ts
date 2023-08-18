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

import { Card } from "@/poker/card";
import { PokerHand } from "@/poker/poker.hand";
import { CardSuit } from "@/poker/card.suit";
import { CardRank } from "@/poker/card.rank";
import { ValueLocator } from "@/poker/value.locator";
import { LocatorHashes } from "@/poker/locator.hashes";

export class CardsSet {
  private static readonly VALUE_OF_RANK_2 = Card.rankToValue(CardRank.RANK_2);

  readonly pokerHand: PokerHand | null;

  readonly value: number;

  constructor(public readonly cards: Card[]) {
    cards.sort((a, b) => a.compareTo(b));
    this.cards = cards;
    const locator = CardsSet.checkLocator(cards);
    if (locator != null) {
      this.pokerHand = locator.pokerHand;
      this.value = cards[locator.index].value;
    } else {
      this.pokerHand = null;
      this.value = 0;
    }
  }

  private static toHash(cards: Card[]): number {
    let prev: Card | null = null;
    let hash = 0;
    let base = 1;
    let count = 0;
    for (let i = cards.length - 1; i >= 0; i--) {
      const card = cards[i];
      if (prev != null && prev.value != card.value) {
        hash += base * count;
        if (hash > 111111111111) {
          return 0;
        }
        base *= 10;
        count = 0;
      }
      count++;
      prev = card;
    }
    hash += base * count;
    return hash;
  }

  private static checkLocator(cards: Card[]): ValueLocator | null {
    const hash = this.toHash(cards);
    const locator = LocatorHashes.getLocator(hash);
    if (locator === null) {
      return null;
    }
    switch (locator.pokerHand) {
      case PokerHand.ROCKET:
        if (cards[0].suit == CardSuit.JOKER) {
          return locator;
        }
        break;

      case PokerHand.STRAIGHT:
        if (this.isStraight(cards, 1)) {
          return locator;
        }
        break;

      case PokerHand.DOUBLE_STRAIGHT:
        if (this.isStraight(cards, 2)) {
          return locator;
        }
        break;

      case PokerHand.TRIPLE_STRAIGHT:
      case PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES:
      case PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS:
        return this.checkAnyTripleStraight(cards);

      default:
        return locator;
    }
    return null;
  }

  private static isStraight(cards: Card[], n: number): boolean {
    const tail = cards[cards.length - 1];
    if (tail.value >= this.VALUE_OF_RANK_2) {
      return false;
    }
    return (tail.value - cards[0].value + 1) * n === cards.length;
  }

  static checkAnyTripleStraight(cards: Card[]): ValueLocator | null {
    let count = 0;
    let continuous = 0;
    let maxContinuous = 0;
    let prevTriple = 0;
    let index = 0;
    for (let i = 0; i < cards.length; i++) {
      const card = cards[i];
      const next = i + 1 === cards.length ? null : cards[i + 1];
      count++;
      if (next === null || card.value != next.value || card.rank === CardRank.RANK_2) {
        const triple = count >= 3 && (prevTriple === 0 || prevTriple + 1 === card.value);
        if (triple) {
          continuous++;
        }
        if (next === null || !triple) {
          if (continuous > maxContinuous) {
            index = i + 1 - continuous * 3 - (triple ? 0 : count);
            maxContinuous = continuous;
          }
          continuous = count >= 3 ? 1 : 0;
        }
        if (count >= 3) {
          prevTriple = card.value;
        }
        count = 0;
      }
    }
    if (maxContinuous * 3 === cards.length) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT, index);
    }
    if (maxContinuous * 4 === cards.length) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, index);
    }
    if (cards.length % 5 == 0 && maxContinuous * 5 >= cards.length) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, index);
    }
    return null;
  }

  dominate(cardsSet: CardsSet): boolean {
    if (this.pokerHand != cardsSet.pokerHand) {
      if (this.pokerHand == PokerHand.ROCKET) {
        return true;
      } else if (cardsSet.pokerHand == PokerHand.ROCKET) {
        return false;
      } else if (this.pokerHand == PokerHand.FOUR_OF_KIND) {
        return true;
      } else if (cardsSet.pokerHand == PokerHand.FOUR_OF_KIND) {
        return false;
      }
    }
    if (this.cards.length != cardsSet.cards.length) {
      return false;
    }
    if (
      this.pokerHand == PokerHand.TRIPLE_STRAIGHT &&
      cardsSet.pokerHand == PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES
    ) {
      return this.cards[3].value > cardsSet.value;
    }
    return this.value > cardsSet.value;
  }
}
