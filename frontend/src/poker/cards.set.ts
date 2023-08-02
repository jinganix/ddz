import { Card } from "@/poker/card";
import { PokerHand } from "@/poker/poker.hand";
import { PokerHandHashes } from "@/poker/poker.hand.hashes";
import { CardSuit } from "@/poker/card.suit";
import { CardRank } from "@/poker/card.rank";

export class CardsSet {
  private readonly pokerHand: PokerHand | null;

  constructor(public readonly cards: Card[]) {
    cards.sort((a, b) => a.compareTo(b));
    this.cards = cards;
    this.pokerHand = this.checkPokerHand(cards);
  }

  getPokerHand(): PokerHand | null {
    return this.pokerHand;
  }

  toHash(cards: Card[]): number {
    let prev: Card | null = null;
    let hash = 0;
    let base = 1;
    let count = 0;
    for (const card of cards) {
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

  checkPokerHand(cards: Card[]): PokerHand | null {
    if (cards.length === 1) {
      return PokerHand.SINGLE;
    }
    const hash = this.toHash(cards);
    const pokerHand = PokerHandHashes.getPokerHand(hash);
    if (pokerHand === null) {
      return null;
    }
    switch (pokerHand) {
      case PokerHand.ROCKET:
        if (cards[0].suit != CardSuit.JOKER) {
          return null;
        }
        break;

      case PokerHand.STRAIGHT:
        if (this.notStraight(cards, 1)) {
          return null;
        }
        break;

      case PokerHand.DOUBLE_STRAIGHT:
        if (this.notStraight(cards, 2)) {
          return null;
        }
        break;

      case PokerHand.TRIPLE_STRAIGHT:
      case PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES:
      case PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS:
        return this.checkStraightThree(cards);

      default:
        return pokerHand;
    }
    return pokerHand;
  }

  notStraight(cards: Card[], n: number): boolean {
    let prev: Card | null = null;
    for (let i = 0; i < cards.length; i += n) {
      const card = cards[i];
      if (card.rank === CardRank.RANK_2) {
        return true;
      }
      if (prev != null && prev.value + 1 != card.value) {
        return true;
      }
      prev = card;
    }
    return false;
  }

  checkStraightThree(cards: Card[]): PokerHand | null {
    let count = 0;
    let continuous = 0;
    let maxContinuous = 0;
    let prevTriple = 0;
    for (let i = 0; i < cards.length; i++) {
      const card = cards[i];
      const next = i + 1 === cards.length ? null : cards[i + 1];
      count++;
      if (next === null || card.value != next.value || card.rank === CardRank.RANK_2) {
        const c = count === 3 && (prevTriple === 0 || prevTriple + 1 === card.value);
        if (c) {
          continuous++;
        }
        if (next === null || !c) {
          if (continuous > maxContinuous) {
            maxContinuous = continuous;
          }
          continuous = count === 3 ? 1 : 0;
        }
        if (count === 3) {
          prevTriple = card.value;
        }
        count = 0;
      }
    }
    if (maxContinuous * 3 === cards.length) {
      return PokerHand.TRIPLE_STRAIGHT;
    }
    if (maxContinuous * 4 === cards.length) {
      return PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES;
    }
    if (maxContinuous * 5 === cards.length) {
      return PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS;
    }
    return null;
  }

  dominate(_cardsSet: CardsSet): boolean {
    return false;
  }
}
