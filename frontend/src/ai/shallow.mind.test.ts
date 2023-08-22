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

import { Card } from "../poker/card";
import { CardsSet } from "../poker/cards.set";
import { ShallowMind } from "./shallow.mind";
import { CardsHelper } from "../../test/cards.helper";

describe("ShallowMind", () => {
  const cardsHelper = new CardsHelper();

  function toCards(str: string): Card[] {
    return cardsHelper.toCards(str);
  }

  beforeEach(() => {
    cardsHelper.initialize();
  });

  describe("popCards", () => {
    describe("when follow suit", () => {
      describe("when there are dominating cards", () => {
        describe("then return cards", () => {
          it.each(
            [
              ["3", "345", "4"],
              ["3", "44", "4"],
              ["4", "3445", "5"],
              ["4", "5566", "5"],
              ["J", "44445", "4444"],
              ["J", "5X", "X"],
              ["J", "5XD", "XD"],
              ["33", "3445", "44"],
              ["33", "444", "44"],
              ["333", "34445", "444"],
              ["333", "34444", "4444"],
              ["333", "5XD", "XD"],
              ["3334", "56667", "6665"],
              ["3334", "56666", "6666"],
              ["3334", "5XD", "XD"],
              ["33388", "344455", "44455"],
              ["33388", "444455", "4444"],
              ["33388", "5XD", "XD"],
              ["3333", "4444567", "4444"],
              ["34567", "8888", "8888"],
              ["34567", "4567890JQKA", "45678"],
              ["34567", "4556789", "45678"],
              ["34567", "45556789", "45678"],
              ["34567", "5XD", "XD"],
              ["45678", "345679999", "9999"],
              ["334455", "34455666777", "445566"],
              ["334455", "77778", "7777"],
              ["334455", "5XD", "XD"],
              ["333444555", "567778889990JJ", "777888999"],
              ["333444555", "77778", "7777"],
              ["333444555789", "567778889990JQKA2", "777888999560"],
              ["3334445566", "67778889900JQ", "7778889900"],
              ["3334445566", "5XD", "XD"],
              ["444456", "5777789", "777758"],
              ["444456", "7777", "7777"],
              ["444456", "567XD", "XD"],
              ["444455", "3333", "3333"],
              ["444455", "3333XD", "XD"],
              ["444455", "3333X", "3333"],
              ["444455", "3333D", "3333"],
              ["444455", "6666XD", "6666"],
              ["444455", "5777788", "777788"],
              ["444455", "7777", "7777"],
              ["444455", "567XD", "XD"],
              ["444455", "77778", "7777"],
              ["44445566", "55777788", "77775588"],
              ["44445566", "77778", "7777"],
              ["44445566", "7777", "7777"],
              ["44445566", "567XD", "XD"],
              ["4444", "55777788", "7777"],
              ["4444", "567XD", "XD"],
            ].map(([a, b, c]) => ({
              cardsStr: a as string,
              expected: c as string,
              handCardsStr: b as string,
            })),
          )("$cardsStr => $expected in $handCardsStr", ({ cardsStr, handCardsStr, expected }) => {
            const cardsSet = new CardsSet(toCards(cardsStr));
            const handCards = toCards(handCardsStr);
            const mind = new ShallowMind(handCards);
            expect(
              mind
                .popCards(cardsSet)
                .map((e) => e.value)
                .sort(),
            ).toEqual(
              new CardsHelper(handCards)
                .toCards(expected)
                .map((e) => e.value)
                .sort(),
            );
          });
        });
      });

      describe("when there are no dominating cards", () => {
        describe("then return empty", () => {
          it.each(
            [
              ["J", "3"],
              ["J", "444456"],
              ["J", "56XD"],
              ["33", "567"],
              ["333", "56XD"],
              ["3334", "5667"],
              ["3334", "56XD"],
              ["55588", "344466"],
              ["5555", "4444678"],
              ["45678", "3456790J"],
              ["45678", "JQKA2"],
              ["45678", "KA2XD"],
              ["45678", "56XD"],
              ["778899", "334455678"],
              ["778899", "56XD"],
              ["666777888", "333444555678"],
              ["666777888", "56XD"],
              ["666777888JQK", "333444555678"],
              ["66677789", "888999J"],
              ["66677789", "7890"],
              ["666777888JQK", "56XD"],
              ["6667778899", "5678"],
              ["3334445566", "888999JJ"],
              ["6667778899", "333444JJQQ"],
              ["6667778899", "56XD"],
              ["4444", "X"],
              ["XD", "3333"],
            ].map(([a, b]) => ({
              cardsStr: a as string,
              handCardsStr: b as string,
            })),
          )("$cardsStr => empty in $handCardsStr", ({ cardsStr, handCardsStr }) => {
            const cardsSet = new CardsSet(toCards(cardsStr));
            const handCards = toCards(handCardsStr);
            const mind = new ShallowMind(handCards);
            expect(mind.popCards(cardsSet).length).toBe(0);
          });
        });
      });
    });
  });
});
