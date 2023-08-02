import { PokerHand } from "@/poker/poker.hand";

export class ValueLocator {
  constructor(
    public pokerHand: PokerHand,
    public index: number,
  ) {}
}
