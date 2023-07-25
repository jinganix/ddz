import { Card } from "@/poker/card";
import { PokerHand } from "@/poker/poker.hand";

export class CardsSet {
  constructor(public readonly cards: Card[]) {}

  getPokerHand(): PokerHand | null {
    return null;
  }
}
