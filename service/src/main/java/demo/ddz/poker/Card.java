package demo.ddz.poker;

import lombok.Getter;

/**
 * Poker card.
 */
@Getter
public class Card implements Comparable<Card> {

  private final int id;

  private final CardSuit suit;

  private final CardRank rank;

  private final int value;

  /**
   * Constructor.
   *
   * @param id card id
   */
  public Card(int id) {
    this.id = id;
    this.suit = CardSuit.values()[(id - 1) / 13];
    if (id == 53) {
      this.rank = CardRank.JOKER_1;
    } else if (id == 54) {
      this.rank = CardRank.JOKER_2;
    } else {
      this.rank = CardRank.values()[(id - 1) % 13 + 1];
    }
    if (rank == CardRank.ACE) {
      this.value = CardRank.QUEEN.ordinal() + 2;
    } else if (rank == CardRank.RANK_2) {
      this.value = CardRank.QUEEN.ordinal() + 3;
    } else if (rank == CardRank.JOKER_1) {
      this.value = CardRank.QUEEN.ordinal() + 4;
    } else if (rank == CardRank.JOKER_2) {
      this.value = CardRank.QUEEN.ordinal() + 5;
    } else {
      this.value = this.rank.ordinal() + 1;
    }
  }

  /**
   * Compare.
   *
   * @param card the object to be compared.
   * @return the value {@code 0} if {@code x == y};
   *         a value less than {@code 0} if {@code x < y}; and
   *         a value greater than {@code 0} if {@code x > y}
   */
  @Override
  public int compareTo(Card card) {
    if (this.value == card.value) {
      return Integer.compare(this.id, card.id);
    }
    return Integer.compare(this.value, card.value);
  }
}
