package demo.ddz.poker;

import demo.ddz.enumeration.Enumeration;
import demo.ddz.enumeration.IntegerEnumMapper;

/**
 * The hand of a card.
 */
public enum CardRank implements Enumeration<Integer> {
  /**
   * Ace.
   */
  ACE(1),

  /**
   * Rank 2.
   */
  RANK_2(2),

  /**
   * Rank 3.
   */
  RANK_3(3),

  /**
   * Rank 4.
   */
  RANK_4(4),

  /**
   * Rank 5.
   */
  RANK_5(5),

  /**
   * Rank 6.
   */
  RANK_6(6),

  /**
   * Rank 7.
   */
  RANK_7(7),

  /**
   * Rank 8.
   */
  RANK_8(8),

  /**
   * Rank 9.
   */
  RANK_9(9),

  /**
   * Rank 10.
   */
  RANK_10(10),

  /**
   * Jack.
   */
  JACK(11),

  /**
   * Queen.
   */
  QUEEN(12),

  /**
   * King.
   */
  KING(13),

  /**
   * Joker.
   */
  JOKER_1(14),

  /**
   * Joker.
   */
  JOKER_2(15);

  private static final IntegerEnumMapper<CardRank> mapper = new IntegerEnumMapper<>(values());

  private final Integer value;

  CardRank(int value) {
    this.value = value;
  }

  /**
   * Get enum from value.
   *
   * @param value {@link Integer}
   * @return {@link CardRank}
   */
  public static CardRank fromValue(Integer value) {
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