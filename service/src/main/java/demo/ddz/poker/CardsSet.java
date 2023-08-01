package demo.ddz.poker;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * A set of cards.
 */
public class CardsSet {

  private final List<Card> cards;

  @Getter
  private final PokerHand pokerHand;

  /**
   * Constructor.
   *
   * @param cards list of {@link Card}
   */
  public CardsSet(List<Card> cards) {
    Collections.sort(cards);
    this.cards = cards;
    this.pokerHand = checkPokerHand(cards);
  }

  private static long toHash(List<Card> cards) {
    Card prev = null;
    long hash = 0;
    long base = 1;
    int count = 0;
    for (Card card : cards) {
      if (prev != null && prev.getValue() != card.getValue()) {
        hash += base * count;
        if (hash > 111111111111L) {
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

  private static PokerHand checkPokerHand(List<Card> cards) {
    if (cards.size() == 1) {
      return PokerHand.SINGLE;
    }
    long hash = toHash(cards);
    PokerHand pokerHand = PokerHandHashes.getPokerHand(hash);
    if (pokerHand == null) {
      return null;
    }
    switch (pokerHand) {
      case ROCKET -> {
        if (cards.get(0).getSuit() != CardSuit.JOKER) {
          return null;
        }
      }
      case STRAIGHT -> {
        if (notStraight(cards, 1)) {
          return null;
        }
      }
      case DOUBLE_STRAIGHT -> {
        if (notStraight(cards, 2)) {
          return null;
        }
      }
      case TRIPLE_STRAIGHT, TRIPLE_STRAIGHT_WITH_SINGLES, TRIPLE_STRAIGHT_WITH_PAIRS -> {
        return checkStraightThree(cards);
      }
      default -> {
        return pokerHand;
      }
    }
    return pokerHand;
  }

  private static boolean notStraight(List<Card> cards, int n) {
    Card prev = null;
    for (int i = 0; i < cards.size(); i += n) {
      Card card = cards.get(i);
      if (card.getRank() == CardRank.RANK_2) {
        return true;
      }
      if (prev != null && prev.getValue() + 1 != card.getValue()) {
        return true;
      }
      prev = card;
    }
    return false;
  }

  private static PokerHand checkStraightThree(List<Card> cards) {
    int count = 0;
    int continuous = 0;
    int maxContinuous = 0;
    int prevTriple = 0;
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      Card next = (i + 1 == cards.size()) ? null : cards.get(i + 1);
      count++;
      if (next == null || card.getValue() != next.getValue() || card.getRank() == CardRank.RANK_2) {
        boolean c = count == 3 && (prevTriple == 0 || prevTriple + 1 == card.getValue());
        if (c) {
          continuous++;
        }
        if (next == null || !c) {
          if (continuous > maxContinuous) {
            maxContinuous = continuous;
          }
          continuous = count == 3 ? 1 : 0;
        }
        if (count == 3) {
          prevTriple = card.getValue();
        }
        count = 0;
      }
    }
    if (maxContinuous * 3 == cards.size()) {
      return PokerHand.TRIPLE_STRAIGHT;
    }
    if (maxContinuous * 4 == cards.size()) {
      return PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES;
    }
    if (maxContinuous * 5 == cards.size()) {
      return PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS;
    }
    return null;
  }
}
