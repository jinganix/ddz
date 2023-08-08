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

import { fill, groupBy, sum } from "lodash";
import { PokerHand } from "@/poker/poker.hand";
import { ValueLocator } from "@/poker/value.locator";

export class LocatorHashes {
  private static LOCATORS: Record<number, ValueLocator>;

  static genOthers(): void {
    this.addLocator(1, PokerHand.SINGLE, 0);
    this.addLocator(2, PokerHand.PAIR, 0);
    this.addLocator(3, PokerHand.THREE_OF_KIND, 0);
    this.addLocator(4, PokerHand.FOUR_OF_KIND, 0);
    this.addLocator(11, PokerHand.ROCKET, 0);
    this.addLocator(31, PokerHand.THREE_WITH_SINGLE, 0);
    this.addLocator(13, PokerHand.THREE_WITH_SINGLE, 1);
    this.addLocator(32, PokerHand.THREE_WITH_PAIR, 0);
    this.addLocator(23, PokerHand.THREE_WITH_PAIR, 2);
    this.addLocator(411, PokerHand.FOUR_WITH_TWO, 0);
    this.addLocator(141, PokerHand.FOUR_WITH_TWO, 1);
    this.addLocator(114, PokerHand.FOUR_WITH_TWO, 2);
    this.addLocator(42, PokerHand.FOUR_WITH_PAIR, 0);
    this.addLocator(24, PokerHand.FOUR_WITH_PAIR, 2);
    this.addLocator(44, PokerHand.FOUR_WITH_TWO_PAIRS, 4);
    this.addLocator(422, PokerHand.FOUR_WITH_TWO_PAIRS, 0);
    this.addLocator(242, PokerHand.FOUR_WITH_TWO_PAIRS, 2);
    this.addLocator(224, PokerHand.FOUR_WITH_TWO_PAIRS, 4);
  }

  static genStraight(): void {
    this.permute("1", 5, 12).forEach((e) => this.addLocator(Number(e), PokerHand.STRAIGHT, 0));
  }

  static genDoubleStraight(): void {
    this.permute("2", 3, 10).forEach((e) =>
      this.addLocator(Number(e), PokerHand.DOUBLE_STRAIGHT, 0),
    );
  }

  static genTripleStraight(): void {
    this.permute("3", 2, 6).forEach((e) =>
      this.addLocator(Number(e), PokerHand.TRIPLE_STRAIGHT, 0),
    );
  }

  static genTripleStraightWithSingles() {
    const triplesList = this.permute("34", 2, 5).map((str) => str.split("").map((e) => Number(e)));

    const singlesListMap = groupBy(
      this.permute("1234", 1, 5)
        .map((str) => str.split("").map((e) => Number(e)))
        .filter((vs) => sum(vs) <= 5),
      (vs) => sum(vs),
    );

    for (const triples of triplesList) {
      const singlesLength = triples.filter((e) => e == 3).length;
      const singlesList = singlesListMap[singlesLength];
      if (!singlesList) {
        this.addLocator(this.toHash(triples), PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, 0);
        continue;
      }

      for (const singles of singlesList) {
        for (let index = 0; index <= singles.length; index++) {
          const location = sum(singles.slice(0, index));
          const cards = [...singles.slice(0, index), ...triples, ...singles.slice(index)];
          this.addLocator(this.toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, location);
        }
      }
    }
  }

  static genTripleStraightWithPairs() {
    const pairsList = ["22", "222", "2222", "4", "44"].map((str) =>
      str.split("").map((e) => Number(e)),
    );
    for (const pairs of pairsList) {
      const straightLength = sum(pairs) / 2;
      for (let index = 0; index <= pairs.length; index++) {
        const cards = [
          ...pairs.slice(0, index),
          ...fill(Array(straightLength), 3),
          ...pairs.slice(index),
        ];
        const location = sum(cards.slice(0, index));
        this.addLocator(this.toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, location);
      }
    }
  }

  static toHash(counts: number[]): number {
    let hash = 0;
    let base = 1;
    for (let i = counts.length - 1; i >= 0; i--) {
      hash += base * counts[i];
      base *= 10;
    }
    return hash;
  }

  static getLocator(hash: number): ValueLocator | null {
    if (!this.LOCATORS) {
      this.LOCATORS = {};
      this.genTripleStraightWithSingles();
      this.genTripleStraightWithPairs();
      this.genStraight();
      this.genDoubleStraight();
      this.genTripleStraight();
      this.genOthers();
    }
    return this.LOCATORS[hash] || null;
  }

  private static addLocator(hash: number, pokerHand: PokerHand, index: number): void {
    this.LOCATORS[hash] = new ValueLocator(pokerHand, index);
  }

  private static permute(str: string, fromLen: number, toLen: number): string[] {
    return Array.from(this.permutation(new Set<string>(), str.split(""), "", fromLen, toLen));
  }

  private static permutation(
    res: Set<string>,
    chars: string[],
    p: string,
    f: number,
    t: number,
  ): Set<string> {
    if (p.length >= f) {
      res.add(p);
    }
    if (p.length == t) {
      return res;
    }
    for (const c of chars) {
      this.permutation(res, chars, p + c, f, t);
    }
    return res;
  }
}
