package demo.ddz.poker;

import demo.ddz.enumeration.Enumeration;
import demo.ddz.enumeration.IntegerEnumMapper;

/**
 * The suit of a card.
 */
public enum CardSuit implements Enumeration<Integer> {

  /**
   * Spades.
   */
  SPADES(1),

  /**
   * Clubs.
   */
  HEARTS(2),

  /**
   * Clubs.
   */
  CLUBS(3),

  /**
   * Diamonds.
   */
  DIAMONDS(4),

  /**
   * Joker.
   */
  JOKER(5);

  private static final IntegerEnumMapper<CardSuit> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  CardSuit(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link CardSuit}
   */
  public static CardSuit fromValue(Integer value) {
    return mapper.fromValue(value);
  }

  /**
   * Get enumeration value.
   *
   * @return {@link Integer}
   */
  @Override
  public Integer getValue() {
    return value;
  }
}
