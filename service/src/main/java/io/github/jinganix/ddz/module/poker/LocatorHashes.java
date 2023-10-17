/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.jinganix.ddz.module.poker;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Hash tables to lookup {@link ValueLocator}. */
public class LocatorHashes {

  private static final Map<Long, ValueLocator> LOCATORS = new HashMap<>();

  static {
    genTripleStraightWithSingles();
    genTripleStraightWithPairs();
    genStraight();
    genDoubleStraight();
    genTripleStraight();
    genOthers();
  }

  private LocatorHashes() {}

  private static void genOthers() {
    addLocator(1L, PokerHand.SINGLE, 0);
    addLocator(2L, PokerHand.PAIR, 0);
    addLocator(3L, PokerHand.THREE_OF_KIND, 0);
    addLocator(4L, PokerHand.FOUR_OF_KIND, 0);
    addLocator(11L, PokerHand.ROCKET, 0);
    addLocator(31L, PokerHand.THREE_WITH_SINGLE, 0);
    addLocator(13L, PokerHand.THREE_WITH_SINGLE, 1);
    addLocator(32L, PokerHand.THREE_WITH_PAIR, 0);
    addLocator(23L, PokerHand.THREE_WITH_PAIR, 2);
    addLocator(411L, PokerHand.FOUR_WITH_TWO, 0);
    addLocator(141L, PokerHand.FOUR_WITH_TWO, 1);
    addLocator(114L, PokerHand.FOUR_WITH_TWO, 2);
    addLocator(42L, PokerHand.FOUR_WITH_PAIR, 0);
    addLocator(24L, PokerHand.FOUR_WITH_PAIR, 2);
    addLocator(44L, PokerHand.FOUR_WITH_TWO_PAIRS, 4);
    addLocator(422L, PokerHand.FOUR_WITH_TWO_PAIRS, 0);
    addLocator(242L, PokerHand.FOUR_WITH_TWO_PAIRS, 2);
    addLocator(224L, PokerHand.FOUR_WITH_TWO_PAIRS, 4);
  }

  private static void genStraight() {
    permute("1", 5, 12).forEach(e -> addLocator(Long.parseLong(e), PokerHand.STRAIGHT, 0));
  }

  private static void genDoubleStraight() {
    permute("2", 3, 10).forEach(e -> addLocator(Long.parseLong(e), PokerHand.DOUBLE_STRAIGHT, 0));
  }

  private static void genTripleStraight() {
    permute("3", 2, 6).forEach(e -> addLocator(Long.parseLong(e), PokerHand.TRIPLE_STRAIGHT, 0));
  }

  private static void genTripleStraightWithSingles() {
    List<List<Integer>> triplesList =
        permute("34", 2, 5).stream()
            .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).toList())
            .toList();

    Map<Integer, List<List<Integer>>> singlesListMap =
        permute("1234", 1, 5).stream()
            .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).toList())
            .filter(values -> sum(values) <= 5)
            .collect(Collectors.groupingBy(LocatorHashes::sum));

    for (List<Integer> triples : triplesList) {
      int singlesLength = (int) triples.stream().filter(e -> e == 3).count();
      List<List<Integer>> singlesList = singlesListMap.get(singlesLength);
      if (singlesList == null) {
        addLocator(toHash(triples), PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, 0);
        continue;
      }

      for (List<Integer> singles : singlesList) {
        for (int index = 0; index <= singles.size(); index++) {
          List<Integer> cards = new LinkedList<>(singles);
          int location = sum(cards.subList(0, index));
          cards.addAll(index, triples);
          addLocator(toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, location);
        }
      }
    }
  }

  private static void genTripleStraightWithPairs() {
    List<List<Integer>> pairsList =
        Stream.of("22", "222", "2222", "4", "44")
            .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).toList())
            .toList();
    for (List<Integer> pairs : pairsList) {
      int straightLength = sum(pairs) / 2;
      for (int index = 0; index <= pairs.size(); index++) {
        List<Integer> cards = new LinkedList<>(pairs);
        int location = sum(cards.subList(0, index));
        cards.addAll(index, Collections.nCopies(straightLength, 3));
        addLocator(toHash(cards), PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, location);
      }
    }
  }

  private static int sum(List<Integer> values) {
    return values.stream().mapToInt(Integer::intValue).sum();
  }

  private static void addLocator(long hash, PokerHand pokerHand, int index) {
    LOCATORS.put(hash, new ValueLocator(pokerHand, index));
  }

  private static Set<String> permute(String str, int fromLen, int toLen) {
    return permutation(new HashSet<>(), str.split(""), "", fromLen, toLen);
  }

  private static Set<String> permutation(Set<String> res, String[] chars, String p, int f, int t) {
    if (p.length() >= f) {
      res.add(p);
    }
    if (p.length() == t) {
      return res;
    }
    for (String c : chars) {
      permutation(res, chars, p + c, f, t);
    }
    return res;
  }

  private static long toHash(List<Integer> counts) {
    long hash = 0;
    long base = 1;
    for (int i = counts.size() - 1; i >= 0; i--) {
      hash += base * counts.get(i);
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
  public static ValueLocator getLocator(Long hash) {
    return LOCATORS.get(hash);
  }
}
