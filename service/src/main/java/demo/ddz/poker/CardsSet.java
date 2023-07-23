package demo.ddz.poker;

import java.util.List;

/**
 * A set of cards.
 */
public class CardsSet {

  private final List<Card> cards;

  /**
   * Constructor.
   *
   * @param cards list of {@link Card}
   */
  public CardsSet(List<Card> cards) {
    this.cards = cards;
  }

  /**
   * Get poker hand of the cards.
   *
   * @return {@link PokerHand}
   */
  public PokerHand getPokerHand() {
    return null;
  }
}
