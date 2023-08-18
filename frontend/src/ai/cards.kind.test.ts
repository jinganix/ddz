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
import { CardsKind } from "./cards.kind";

describe("CardsKind", () => {
  describe("size", () => {
    describe("when get size", () => {
      it("then return cards size", () => {
        const kind = new CardsKind(5, [new Card(5)]);
        expect(kind.size()).toEqual(1);
      });
    });
  });

  describe("push", () => {
    describe("when push a card", () => {
      it("then it is in tail", () => {
        const kind = new CardsKind(5, [new Card(5)]);
        kind.push(new Card(18));
        expect(kind.cards[0].id).toEqual(5);
        expect(kind.cards[1].id).toEqual(18);
      });
    });
  });

  describe("pop", () => {
    describe("when pop 2 cards", () => {
      it("then 2 cards are popped", () => {
        const kind = new CardsKind(5, [new Card(5), new Card(18), new Card(31)]);
        const cards = kind.pop(2);
        expect(cards.length).toEqual(2);
        expect(kind.size()).toEqual(1);
      });
    });
  });
});
