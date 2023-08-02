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

import { CardSuit } from "./card.suit";
import { CardRank } from "./card.rank";
import { Card } from "./card";

describe("Card", () => {
  describe("constructor", () => {
    describe("when card id is valid", () => {
      describe.each([
        [1, CardSuit.SPADES, CardRank.ACE, 14],
        [14, CardSuit.HEARTS, CardRank.ACE, 14],
        [27, CardSuit.CLUBS, CardRank.ACE, 14],
        [40, CardSuit.DIAMONDS, CardRank.ACE, 14],
        [2, CardSuit.SPADES, CardRank.RANK_2, 15],
        [41, CardSuit.DIAMONDS, CardRank.RANK_2, 15],
        [3, CardSuit.SPADES, CardRank.RANK_3, 3],
        [13, CardSuit.SPADES, CardRank.KING, 13],
        [52, CardSuit.DIAMONDS, CardRank.KING, 13],
        [53, CardSuit.JOKER, CardRank.JOKER_1, 16],
        [54, CardSuit.JOKER, CardRank.JOKER_2, 17],
      ])("when card id is %d", (cardId: number, suit: CardSuit, rank: CardRank, value: number) => {
        const card = new Card(cardId);
        it(`then construct card {suit: ${suit}, rank: ${rank}, value: ${value}}`, () => {
          expect(card.id).toEqual(cardId);
          expect(card.suit).toEqual(suit);
          expect(card.rank).toEqual(rank);
          expect(card.value).toEqual(value);
        });
      });
    });

    describe("when card id is invalid", () => {
      describe("when card id is 0", () => {
        it("then throw exception", () => {
          expect(() => new Card(0)).toThrowError("Card id should be from 1 to 54");
        });
      });

      describe("when card id is 55", () => {
        it("then throw exception", () => {
          expect(() => new Card(55)).toThrowError("Card id should be from 1 to 54");
        });
      });
    });
  });

  describe("compareTo", () => {
    describe("when cards pairs is provided", () =>
      describe.each([
        [0, new Card(54), new Card(54)],
        [1, new Card(54), new Card(53)],
        [-1, new Card(53), new Card(54)],
        [1, new Card(14), new Card(1)],
        [-1, new Card(1), new Card(14)],
        [1, new Card(2), new Card(1)],
        [1, new Card(1), new Card(13)],
        [1, new Card(1), new Card(3)],
        [1, new Card(4), new Card(3)],
        [1, new Card(13), new Card(12)],
        [1, new Card(1), new Card(13)],
        [1, new Card(53), new Card(2)],
        [1, new Card(53), new Card(1)],
      ])("then return expected compare result", (expected: number, a: Card, b: Card) => {
        expect(a.compareTo(b)).toEqual(expected);
      }));
  });
});
