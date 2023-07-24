import { CardSuit } from "@/poker/card.suit";
import { CardRank } from "@/poker/card.rank";

export class Card {
  readonly id: number;

  readonly suit: CardSuit;

  readonly rank: CardRank;

  readonly value: number;

  constructor(id: number) {
    if (id < 1 || id > 54) {
      throw new Error("Card id should be from 1 to 54");
    }
    this.id = id;
    this.suit = Math.floor((id - 1) / 13) + 1;
    if (id == 53) {
      this.rank = CardRank.JOKER_1;
    } else if (id == 54) {
      this.rank = CardRank.JOKER_2;
    } else {
      this.rank = ((id - 1) % 13) + 1;
    }
    if (this.rank == CardRank.ACE) {
      this.value = CardRank.KING + 1;
    } else if (this.rank == CardRank.RANK_2) {
      this.value = CardRank.KING + 2;
    } else if (this.rank == CardRank.JOKER_1) {
      this.value = CardRank.KING + 3;
    } else if (this.rank == CardRank.JOKER_2) {
      this.value = CardRank.KING + 4;
    } else {
      this.value = this.rank;
    }
  }

  compareTo(card: Card): number {
    if (this.value === card.value) {
      return this.id === card.id ? 0 : this.id - card.id > 0 ? 1 : -1;
    }
    return this.value - card.value > 0 ? 1 : -1;
  }
}
