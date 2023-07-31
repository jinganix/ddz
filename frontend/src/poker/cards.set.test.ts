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
            ["334455", PokerHand.DOUBLE_STRAIGHT],
            ["3344556677889900JJQQ", PokerHand.DOUBLE_STRAIGHT],
            ["AAAA", PokerHand.FOUR_OF_KIND],
            ["AAAAXD", PokerHand.FOUR_WITH_TWO],
            ["2AAAA2", PokerHand.FOUR_WITH_PAIR],
            ["AAAA2233", PokerHand.FOUR_WITH_TWO_PAIRS],
            ["77888899", PokerHand.FOUR_WITH_TWO_PAIRS],
            ["AA", PokerHand.PAIR],
            ["XD", PokerHand.ROCKET],
            ["A", PokerHand.SINGLE],
            ["34567", PokerHand.STRAIGHT],
            ["34567890JQKA", PokerHand.STRAIGHT],
            ["AAA", PokerHand.THREE_OF_KIND],
            ["AAA99", PokerHand.THREE_WITH_PAIR],
            ["9AAA9", PokerHand.THREE_WITH_PAIR],
            ["AAAX", PokerHand.THREE_WITH_SINGLE],
            ["2AAA", PokerHand.THREE_WITH_SINGLE],
            ["333444555", PokerHand.TRIPLE_STRAIGHT],
            ["333444555666", PokerHand.TRIPLE_STRAIGHT],
            ["333444555778899", PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS],
            ["33344455566677789JKA", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555777", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555778", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["333444555789", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["QQQKKKAAA222", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
            ["33344455", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES],
          ].map(([a, b]) => ({ expected: PokerHand[b as PokerHand], input: a as string })),
        )("$input => $expected", ({ input, expected }) => {
          const expectedEnum = PokerHand[expected as keyof typeof PokerHand];
          expect(new CardsSet(toCards(input)).getPokerHand()).toEqual(expectedEnum);
        });
      });
    });

    describe("when a invalid cards set is provided", () => {
      describe("then return null poker hand", () => {
        it.each([
          ["34"],
          ["3X"],
          ["3D"],
          ["A2"],
          ["AAAXD"],
          ["22223333"],
          ["AAAAX"],
          ["23456789JQK"],
          ["3456789JQKA2"],
          ["2233"],
          ["223344"],
          ["33344455578"],
          ["3334445557890"],
          ["3334445557788"],
          ["33344455577889900"],
          ["33344455566677788889"],
          ["234567890JQKAXD"],
          ["AAA22257"],
          ["QQKKAA22"],
          ["JQKA2"],
        ])("%s => null", (input: string) => {
          expect(new CardsSet(toCards(input)).getPokerHand()).toBeNull();
        });
      });
    });
  });
});
