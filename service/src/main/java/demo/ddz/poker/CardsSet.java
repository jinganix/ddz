/*
 * Copyright (c) 2020 jinganix@qq.com, All Rights Reserved.
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

package demo.ddz.poker;

import static demo.ddz.poker.PokerHand.FOUR_OF_KIND;
import static demo.ddz.poker.PokerHand.ROCKET;
import static demo.ddz.poker.PokerHand.TRIPLE_STRAIGHT;
import static demo.ddz.poker.PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * A set of cards.
 */
public class CardsSet {

  private static final int VALUE_OF_RANK_2 = Card.rankToValue(CardRank.RANK_2);

  private final List<Card> cards;

  @Getter
  private final PokerHand pokerHand;

  private final int value;

  /**
   * Constructor.
   *
   * @param cards list of {@link Card}
   */
  public CardsSet(List<Card> cards) {
    Collections.sort(cards);
    this.cards = cards;
    ValueLocator locator = checkLocator(cards);
    if (locator != null) {
      this.pokerHand = locator.pokerHand();
      this.value = cards.get(locator.index()).getValue();
    } else {
      this.pokerHand = null;
      this.value = 0;
    }
  }

  private static long toHash(List<Card> cards) {
    Card prev = null;
    long hash = 0;
    long base = 1;
    int count = 0;
    for (int i = cards.size() - 1; i >= 0; i--) {
      Card card = cards.get(i);
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

  private static ValueLocator checkLocator(List<Card> cards) {
    long hash = toHash(cards);
    ValueLocator locator = LocatorHashes.getLocator(hash);
    if (locator == null) {
      return null;
    }
    switch (locator.pokerHand()) {
      case ROCKET -> {
        if (cards.get(0).getSuit() == CardSuit.JOKER) {
          return locator;
        }
      }
      case STRAIGHT -> {
        if (isStraight(cards, 1)) {
          return locator;
        }
      }
      case DOUBLE_STRAIGHT -> {
        if (isStraight(cards, 2)) {
          return locator;
        }
      }
      case TRIPLE_STRAIGHT, TRIPLE_STRAIGHT_WITH_SINGLES, TRIPLE_STRAIGHT_WITH_PAIRS -> {
        return checkAnyTripleStraight(cards);
      }
      default -> {
        return locator;
      }
    }
    return null;
  }

  private static boolean isStraight(List<Card> cards, int n) {
    Card tail = cards.get(cards.size() - 1);
    if (tail.getValue() >= VALUE_OF_RANK_2) {
      return false;
    }
    return (tail.getValue() - cards.get(0).getValue() + 1) * n == cards.size();
  }

  private static ValueLocator checkAnyTripleStraight(List<Card> cards) {
    int count = 0;
    int continuous = 0;
    int maxContinuous = 0;
    int prevTriple = 0;
    int index = 0;
    for (int i = 0; i < cards.size(); i++) {
      Card card = cards.get(i);
      Card next = (i + 1 == cards.size()) ? null : cards.get(i + 1);
      count++;
      if (next == null || card.getValue() != next.getValue() || card.getRank() == CardRank.RANK_2) {
        boolean triple = count >= 3 && (prevTriple == 0 || prevTriple + 1 == card.getValue());
        if (triple) {
          continuous++;
        }
        if (next == null || !triple) {
          if (continuous > maxContinuous) {
            index = i + 1 - continuous * 3 - (triple ? 0 : count);
            maxContinuous = continuous;
          }
          continuous = count >= 3 ? 1 : 0;
        }
        if (count >= 3) {
          prevTriple = card.getValue();
        }
        count = 0;
      }
    }
    if (maxContinuous * 3 == cards.size()) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT, index);
    }
    if (maxContinuous * 4 == cards.size()) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, index);
    }
    if (cards.size() % 5 == 0 && maxContinuous * 5 >= cards.size()) {
      return new ValueLocator(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, index);
    }
    return null;
  }

  /**
   * Check whether this cards set dominate the other.
   *
   * @param cardsSet {@link CardsSet}
   * @return true if dominating
   */
  public boolean dominate(CardsSet cardsSet) {
    if (this.pokerHand != cardsSet.pokerHand) {
      if (this.pokerHand == ROCKET) {
        return true;
      } else if (cardsSet.pokerHand == ROCKET) {
        return false;
      } else if (this.pokerHand == FOUR_OF_KIND) {
        return true;
      } else if (cardsSet.pokerHand == FOUR_OF_KIND) {
        return false;
      }
    }
    if (this.cards.size() != cardsSet.cards.size()) {
      return false;
    }
    if (this.pokerHand == TRIPLE_STRAIGHT && cardsSet.pokerHand == TRIPLE_STRAIGHT_WITH_SINGLES) {
      return this.cards.get(3).getValue() > cardsSet.value;
    }
    return this.value > cardsSet.value;
  }
}
