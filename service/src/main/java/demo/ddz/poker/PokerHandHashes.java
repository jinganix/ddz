package demo.ddz.poker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Poker hand hashes.
 */
public class PokerHandHashes {

  private static final Map<Long, PokerHand> HAND_MAP = new HashMap<>();

  static {
    genPokerHands();
    genStraightSingle();
    genDoubleStraight();
    genTripleStraight();
    genTripleStraightWithSingles();
    genTripleStraightWithPairs();
  }

  private PokerHandHashes() {
  }

  private static void genPokerHands() {
    HAND_MAP.put(1L, PokerHand.SINGLE);
    HAND_MAP.put(2L, PokerHand.PAIR);
    HAND_MAP.put(3L, PokerHand.THREE_OF_KIND);
    HAND_MAP.put(4L, PokerHand.FOUR_OF_KIND);
    HAND_MAP.put(11L, PokerHand.ROCKET);
    HAND_MAP.put(31L, PokerHand.THREE_WITH_SINGLE);
    HAND_MAP.put(13L, PokerHand.THREE_WITH_SINGLE);
    HAND_MAP.put(32L, PokerHand.THREE_WITH_PAIR);
    HAND_MAP.put(23L, PokerHand.THREE_WITH_PAIR);
    HAND_MAP.put(411L, PokerHand.FOUR_WITH_TWO);
    HAND_MAP.put(141L, PokerHand.FOUR_WITH_TWO);
    HAND_MAP.put(114L, PokerHand.FOUR_WITH_TWO);
    HAND_MAP.put(42L, PokerHand.FOUR_WITH_PAIR);
    HAND_MAP.put(24L, PokerHand.FOUR_WITH_PAIR);
    HAND_MAP.put(422L, PokerHand.FOUR_WITH_TWO_PAIRS);
    HAND_MAP.put(242L, PokerHand.FOUR_WITH_TWO_PAIRS);
    HAND_MAP.put(224L, PokerHand.FOUR_WITH_TWO_PAIRS);
  }

  private static void genStraightSingle() {
    StringBuilder builder = new StringBuilder("11111");
    while (builder.length() <= 12) {
      HAND_MAP.put(Long.parseLong(builder.toString()), PokerHand.STRAIGHT);
      builder.append("1");
    }
  }

  private static void genDoubleStraight() {
    StringBuilder builder = new StringBuilder("222");
    while (builder.length() <= 10) {
      HAND_MAP.put(Long.parseLong(builder.toString()), PokerHand.DOUBLE_STRAIGHT);
      builder.append("2");
    }
  }

  private static void genTripleStraight() {
    StringBuilder builder = new StringBuilder("33");
    while (builder.length() <= 6) {
      HAND_MAP.put(Long.parseLong(builder.toString()), PokerHand.TRIPLE_STRAIGHT);
      builder.append("3");
    }
  }

  private static void genTripleStraightWithSingles() {
    List<List<Integer>> singlesList = Stream.of(
          "2",
          "11",
          "111",
          "12",
          "21",
          "1111",
          "211",
          "121",
          "112",
          "13",
          "31",
          "11111",
          "2111",
          "1211",
          "1121",
          "1112",
          "311",
          "131",
          "113",
          "32",
          "23"
        )
        .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).toList())
        .toList();
    for (List<Integer> singles : singlesList) {
      int straightLength = singles.stream().mapToInt(Integer::intValue).sum();
      for (int index = 0; index <= singles.size(); index++) {
        List<Integer> cards = new LinkedList<>(singles);
        for (int i = 0; i < straightLength; i++) {
          cards.add(index, 3);
        }
        HAND_MAP.put(toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES);
      }
    }
  }

  private static void genTripleStraightWithPairs() {
    List<List<Integer>> pairsList = Stream.of(
            "22",
            "222",
            "2222"
          )
          .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).toList())
          .toList();
    for (List<Integer> pairs : pairsList) {
      for (int index = 0; index <= pairs.size(); index++) {
        List<Integer> cards = new LinkedList<>(pairs);
        for (int i = 0; i < pairs.size(); i++) {
          cards.add(index, 3);
        }
        HAND_MAP.put(toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS);
      }
    }
  }

  private static long toHash(List<Integer> counts) {
    long hash = 0;
    long base = 1;
    for (Integer count : counts) {
      hash += base * count;
      base *= 10;
    }
    return hash;
  }

  /**
   * Get the {@link PokerHand} by hash coarsely, some need to check more.
   *
   * @param hash hash
   * @return {@link PokerHand}
   */
  public static PokerHand getPokerHand(Long hash) {
    return HAND_MAP.get(hash);
  }
}
