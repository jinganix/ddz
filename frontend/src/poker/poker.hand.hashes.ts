import { PokerHand } from "@/poker/poker.hand";

export class PokerHandHashes {
  private static HAND_MAP: Record<number, PokerHand>;

  static genPokerHands(): void {
    this.HAND_MAP[1] = PokerHand.SINGLE;
    this.HAND_MAP[2] = PokerHand.PAIR;
    this.HAND_MAP[3] = PokerHand.THREE_OF_KIND;
    this.HAND_MAP[4] = PokerHand.FOUR_OF_KIND;
    this.HAND_MAP[11] = PokerHand.ROCKET;
    this.HAND_MAP[31] = PokerHand.THREE_WITH_SINGLE;
    this.HAND_MAP[13] = PokerHand.THREE_WITH_SINGLE;
    this.HAND_MAP[32] = PokerHand.THREE_WITH_PAIR;
    this.HAND_MAP[23] = PokerHand.THREE_WITH_PAIR;
    this.HAND_MAP[411] = PokerHand.FOUR_WITH_TWO;
    this.HAND_MAP[141] = PokerHand.FOUR_WITH_TWO;
    this.HAND_MAP[114] = PokerHand.FOUR_WITH_TWO;
    this.HAND_MAP[42] = PokerHand.FOUR_WITH_PAIR;
    this.HAND_MAP[24] = PokerHand.FOUR_WITH_PAIR;
    this.HAND_MAP[422] = PokerHand.FOUR_WITH_TWO_PAIRS;
    this.HAND_MAP[242] = PokerHand.FOUR_WITH_TWO_PAIRS;
    this.HAND_MAP[224] = PokerHand.FOUR_WITH_TWO_PAIRS;
  }

  static genStraightSingle(): void {
    let str = "11111";
    while (str.length <= 12) {
      this.HAND_MAP[Number(str)] = PokerHand.STRAIGHT;
      str += "1";
    }
  }

  static genDoubleStraight(): void {
    let str = "222";
    while (str.length <= 10) {
      this.HAND_MAP[Number(str)] = PokerHand.DOUBLE_STRAIGHT;
      str += "2";
    }
  }

  static genTripleStraight(): void {
    let str = "33";
    while (str.length <= 6) {
      this.HAND_MAP[Number(str)] = PokerHand.TRIPLE_STRAIGHT;
      str += "3";
    }
  }

  static genTripleStraightWithSingles() {
    const singlesList = [
      "2",
      "11",
      "111",
      "12",
      "21",
      "1111",
      "211",
      "121",
      "112",
      "13",
      "31",
      "11111",
      "2111",
      "1211",
      "1121",
      "1112",
      "311",
      "131",
      "113",
      "32",
      "23",
    ].map((str) => str.split("").map((e) => Number(e)));
    for (const singles of singlesList) {
      const straightLength = singles.reduce((sum, v) => sum + v, 0);
      for (let index = 0; index <= singles.length; index++) {
        const cards = [...singles];
        for (let i = 0; i < straightLength; i++) {
          cards.splice(index, 0, 3);
        }
        this.HAND_MAP[this.toHash(cards)] = PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES;
      }
    }
  }

  static genTripleStraightWithPairs() {
    const pairsList = ["22", "222", "2222"].map((str) => str.split("").map((e) => Number(e)));
    for (const pairs of pairsList) {
      for (let index = 0; index <= pairs.length; index++) {
        const cards = [...pairs];
        for (let i = 0; i < pairs.length; i++) {
          cards.splice(index, 0, 3);
        }
        this.HAND_MAP[this.toHash(cards)] = PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS;
      }
    }
  }

  static toHash(counts: number[]): number {
    let hash = 0;
    let base = 1;
    for (const count of counts) {
      hash += base * count;
      base *= 10;
    }
    return hash;
  }

  static getPokerHand(hash: number): PokerHand | null {
    if (!this.HAND_MAP) {
      this.HAND_MAP = {};
      this.genPokerHands();
      this.genStraightSingle();
      this.genDoubleStraight();
      this.genTripleStraight();
      this.genTripleStraightWithSingles();
      this.genTripleStraightWithPairs();
    }
    return this.HAND_MAP[hash] || null;
  }
}
