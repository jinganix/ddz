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

import { CardRank } from "./card.rank";
import { Card } from "./card";
import { PokerHand } from "./poker.hand";
import { CardsSet } from "./cards.set";

describe("CardSet", () => {
  const CARDS_MAP = {} as Record<CardRank, Card[]>;

  const RANK_MAP = {
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

  function toCards(str: string): Card[] {
    const parts = str.split("");
    const cards: Card[] = [];
    for (const part of parts) {
      const rank: CardRank = RANK_MAP[part];
      const cardsOfRank = CARDS_MAP[rank];
      cards.push(cardsOfRank.pop()!);
    }
    return cards;
  }

  beforeEach(() => {
    Object.keys(CardRank)
      .filter((e) => !isNaN(Number(e)))
      .forEach((k) => (CARDS_MAP[k as unknown as CardRank] = []));
    for (let id = 1; id <= 54; id++) {
      const card = new Card(id);
      CARDS_MAP[card.rank].push(card);
    }
  });

  describe("getPokerHand", () => {
    describe("when a valid cards set is provided", () => {
      describe("then return the poker hand", () => {
        it.each(
          [
            [PokerHand.DOUBLE_STRAIGHT, "334455"],
            [PokerHand.DOUBLE_STRAIGHT, "3344556677889900JJQQ"],
            [PokerHand.FOUR_OF_KIND, "AAAA"],
            [PokerHand.FOUR_WITH_PAIR, "AAAA22"],
            [PokerHand.FOUR_WITH_PAIR, "JJAAAA"],
            [PokerHand.FOUR_WITH_TWO, "AAAAXD"],
            [PokerHand.FOUR_WITH_TWO, "JAAAAX"],
            [PokerHand.FOUR_WITH_TWO, "JQAAAA"],
            [PokerHand.FOUR_WITH_TWO_PAIRS, "66778888"],
            [PokerHand.FOUR_WITH_TWO_PAIRS, "77888899"],
            [PokerHand.FOUR_WITH_TWO_PAIRS, "88889999"],
            [PokerHand.FOUR_WITH_TWO_PAIRS, "8888JJQQ"],
            [PokerHand.PAIR, "AA"],
            [PokerHand.ROCKET, "XD"],
            [PokerHand.SINGLE, "A"],
            [PokerHand.STRAIGHT, "34567"],
            [PokerHand.STRAIGHT, "34567890JQKA"],
            [PokerHand.THREE_OF_KIND, "AAA"],
            [PokerHand.THREE_WITH_PAIR, "99AAA"],
            [PokerHand.THREE_WITH_PAIR, "AAA22"],
            [PokerHand.THREE_WITH_SINGLE, "2AAA"],
            [PokerHand.THREE_WITH_SINGLE, "AAAX"],
            [PokerHand.TRIPLE_STRAIGHT, "333444555"],
            [PokerHand.TRIPLE_STRAIGHT, "333444555666777888"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "33334444666777888999"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "3333444555"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444555666JJJJQQQQ"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444555778899"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444JJJJ"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "3355566688"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333344445555"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33334444555566667777"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3333444455556666777J"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3334444555566667777J"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444455559"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33344455"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3334445556666JJJ"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33344455566677789JKA"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555666777JJJJQ"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555666JJJJ"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555777"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555778"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555789"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333555666777"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3335556667778888"],
            [PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "QQQKKKAAA222"],
          ].map(([a, b]) => ({ expected: PokerHand[a as PokerHand], input: b as string })),
        )("$input => $expected", ({ input, expected }) => {
          const expectedEnum = PokerHand[expected as keyof typeof PokerHand];
          expect(new CardsSet(toCards(input)).getPokerHand()).toEqual(expectedEnum);
        });
      });
    });

    describe("when a invalid cards set is provided", () => {
      describe("then return null poker hand", () => {
        it.each([
          ["2233"],
          ["223344"],
          ["234567890JQKAXD"],
          ["23456789JQK"],
          ["3334445557788"],
          ["33344455577889900"],
          ["33344455578"],
          ["3334445557890"],
          ["333666777"],
          ["333666777999"],
          ["333666777JJQQKK"],
          ["34"],
          ["3456789JQKA2"],
          ["3D"],
          ["3X"],
          ["A2"],
          ["AAA22257"],
          ["AAAAX"],
          ["AAAXD"],
          ["JQKA2"],
          ["QQKKAA22"],
        ])("%s => null", (input: string) => {
          expect(new CardsSet(toCards(input)).getPokerHand()).toBeNull();
        });
      });
    });
  });

  describe("dominate", () => {
    describe("when this cards set is dominating", () => {
      describe("then return true", () => {
        it.each(
          [
            ["3333", "0JQKA"],
            ["3333", "22"],
            ["3333", "222"],
            ["3333", "222A"],
            ["3333", "222AA"],
            ["3333", "44445555"],
            ["3333", "444555"],
            ["3333", "44455566"],
            ["3333", "4445556666"],
            ["3333", "4445556677"],
            ["3333", "D"],
            ["3333", "QQKKAA"],
            ["3333", "QQQKKKAAA"],
            ["4", "3"],
            ["44", "33"],
            ["444", "333"],
            ["4444", "3333"],
            ["4445", "3336"],
            ["44455", "33366"],
            ["445566", "334455"],
            ["45678", "34567"],
            ["55558888", "66667777"],
            ["666677", "4444JJ"],
            ["66667788", "4444JJQQ"],
            ["666678", "4444JQ"],
            ["66668888", "4444JJQQ"],
            ["666777888", "333444555"],
            ["6667778888", "444555JJQQ"],
            ["666777888999", "333444555JJJ"],
            ["6667778899", "444555JJQQ"],
            ["XD", "3333"],
          ].map(([a, b]) => ({ a: a as string, b: b as string })),
        )("$a > $b", ({ a, b }) => {
          expect(new CardsSet(toCards(a)).dominate(new CardsSet(toCards(b)))).toBeTruthy();
        });
      });
    });

    describe("when this cards set is not dominating", () => {
      describe("then return false", () => {
        it.each(
          [
            ["3", "4"],
            ["33", "33"],
            ["33", "4"],
            ["333", "4"],
            ["333444", "4"],
            ["333444555666", "777888999JJJ"],
            ["3334446666", "4"],
            ["334455", "4"],
            ["34567", "4"],
            ["34567", "AAAA"],
            ["3579J", "4"],
            ["4", "4"],
            ["555522", "4"],
            ["55552233", "4"],
            ["55556666", "4"],
            ["AAAA", "XD"],
          ].map(([a, b]) => ({ a: a as string, b: b as string })),
        )("$a <= $b", ({ a, b }) => {
          expect(new CardsSet(toCards(a)).dominate(new CardsSet(toCards(b)))).toBeFalsy();
        });
      });
    });
  });
});
