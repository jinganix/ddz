import { CardRank } from "./card.rank";
import { Card } from "./card";
import { PokerHand } from "./poker.hand";
import { CardSet } from "./card.set";

describe("CardSet", () => {
  const cardsMap = {} as Record<CardRank, Card[]>;

  function toCards(str: string): Card[] {
    const parts = str.split("");
    const cards: Card[] = [];
    for (const part of parts) {
      const rank: CardRank = parseInt(part, 16);
      const cardsOfRank = cardsMap[rank];
      cards.push(cardsOfRank[0]);
    }
    return cards;
  }

  beforeAll(() => {
    for (let id = 1; id <= 54; id++) {
      const card = new Card(id);
      cardsMap[card.rank] || (cardsMap[card.rank] = []);
      cardsMap[card.rank].push(card);
    }
  });

  describe("getPokerHand", () => {
    describe.skip("when a valid cards set is provided", () => {
      describe("then return the poker hand", () => {
        it.each(
          [
            ["334455", PokerHand.DOUBLE_STRAIGHT],
            ["33445566778899AABBCC", PokerHand.DOUBLE_STRAIGHT],
            ["1111", PokerHand.FOUR_OF_KIND],
            ["1111EF", PokerHand.FOUR_WITH_TWO],
            ["211112", PokerHand.FOUR_WITH_PAIR],
            ["11112233", PokerHand.FOUR_WITH_TOW_PAIRS],
            ["11", PokerHand.PAIR],
            ["EF", PokerHand.ROCKET],
            ["1", PokerHand.SINGLE],
            ["34567", PokerHand.STRAIGHT],
            ["3456789ABCD1", PokerHand.STRAIGHT],
            ["111", PokerHand.THREE_OF_KIND],
            ["11199", PokerHand.THREE_WITH_PAIR],
            ["91119", PokerHand.THREE_WITH_PAIR],
            ["111E", PokerHand.THREE_WITH_SINGLE],
            ["2111", PokerHand.THREE_WITH_SINGLE],
            ["333444555", PokerHand.TRIPLE_STRAIGHT],
            ["333444555666", PokerHand.TRIPLE_STRAIGHT],
            ["333444555778899", PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS],
            ["33344455566677789ABC", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555777", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555778", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555789", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
          ].map(([a, b]) => ({ expected: PokerHand[b as PokerHand], input: a as string })),
        )("$input => $expected", ({ input, expected }) => {
          const expectedEnum = PokerHand[expected as keyof typeof PokerHand];
          expect(new CardSet(toCards(input)).getPokerHand()).toEqual(expectedEnum);
        });
      });
    });

    describe("when a invalid cards set is provided", () => {
      describe("then return null poker hand", () => {
        it.each([
          ["12"],
          ["111EF"],
          ["1111EF"],
          ["11112222"],
          ["1111E"],
          ["23456789ABCD"],
          ["3456789ABCD12"],
          ["2233"],
          ["223344"],
          ["333444555789A"],
          ["3334445557788"],
          ["333444555778899AA"],
          ["33344455566677788889"],
        ])("%s => null", (input: string) => {
          expect(new CardSet(toCards(input)).getPokerHand()).toBeNull();
        });
      });
    });
  });
});
