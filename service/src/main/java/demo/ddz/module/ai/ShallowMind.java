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

package demo.ddz.module.ai;

import demo.ddz.module.poker.Card;
import demo.ddz.module.poker.CardRank;
import demo.ddz.module.poker.CardsSet;
import demo.ddz.module.poker.PokerHand;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ShallowMind implements AutoPlay {

  private static final int VALUE_OF_RANK_2 = Card.rankToValue(CardRank.RANK_2);

  private static final int VALUE_OF_JOKER_1 = Card.rankToValue(CardRank.JOKER_1);

  private static final int VALUE_OF_JOKER_2 = Card.rankToValue(CardRank.JOKER_2);

  private final Map<Integer, CardsKind> kinds = new TreeMap<>();

  public ShallowMind(List<Card> cards) {
    cards.stream()
        .collect(Collectors.groupingBy(Card::getValue, Collectors.toCollection(ArrayList::new)))
        .entrySet()
        .stream()
        .map(e -> new CardsKind(e.getKey(), e.getValue()))
        .forEach(e -> this.kinds.put(e.getValue(), e));
  }

  @Override
  public boolean isEmpty() {
    for (CardsKind kind : kinds.values()) {
      if (!kind.getCards().isEmpty()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<Card> toCards() {
    return kinds.values().stream()
        .map(CardsKind::getCards)
        .flatMap(List::stream)
        .sorted()
        .collect(Collectors.toList());
  }

  @Override
  public List<Card> leadOff() {
    List<Card> handCards =
        kinds.values().stream()
            .map(CardsKind::getCards)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    if (new CardsSet(handCards).getPokerHand() != null) {
      kinds.clear();
      return handCards;
    }
    List<Card> cards = popThreeWith();
    if (!cards.isEmpty()) {
      return cards;
    }
    cards = popMaxStraight(0, 1, 5);
    if (!cards.isEmpty()) {
      return cards;
    }
    cards = popMaxStraight(0, 2, 3);
    if (!cards.isEmpty()) {
      return cards;
    }
    cards = popKind(0, 1, 1);
    if (!cards.isEmpty()) {
      return cards;
    }
    return popKind(0, 2, 2);
  }

  @Override
  public List<Card> followSuit(CardsSet cardsSet) {
    return popCards(cardsSet.getPokerHand(), cardsSet.getValue(), cardsSet.getCards().size());
  }

  private List<Card> popThreeWith() {
    List<Card> cards = popKind(0, 3, 3);
    if (cards.isEmpty()) {
      return cards;
    }
    List<Card> popped = popKind(0, 1, 1);
    if (!popped.isEmpty()) {
      cards.addAll(popped);
      return cards;
    }
    popped = popKind(0, 2, 2);
    cards.addAll(popped);
    return cards;
  }

  private List<Card> popCards(PokerHand pokerHand, int minValue, int cardSize) {
    List<Card> cards = matchPokerHand(pokerHand, minValue, cardSize);
    if (!cards.isEmpty() || pokerHand == PokerHand.FOUR_OF_KIND || pokerHand == PokerHand.ROCKET) {
      return cards;
    }
    return mayPopBomb();
  }

  private List<Card> matchPokerHand(PokerHand pokerHand, int minValue, int cardSize) {
    return switch (pokerHand) {
      case SINGLE -> popKind(minValue, 1, 3);
      case PAIR -> popKind(minValue, 2, 3);
      case THREE_OF_KIND -> popKind(minValue, 3, 3);
      case THREE_WITH_SINGLE -> {
        List<Card> cards = popKind(minValue, 3, 3);
        if (cards.isEmpty()) {
          yield cards;
        }
        yield popOrRevert(cards, 1);
      }
      case THREE_WITH_PAIR -> {
        List<Card> cards = popKind(minValue, 3, 3);
        if (cards.isEmpty()) {
          yield cards;
        }
        yield popOrRevert(cards, 2);
      }
      case STRAIGHT -> popStraight(minValue, 1, cardSize);
      case DOUBLE_STRAIGHT -> popStraight(minValue, 2, cardSize / 2);
      case TRIPLE_STRAIGHT -> popStraight(minValue, 3, cardSize / 3);
      case TRIPLE_STRAIGHT_WITH_SINGLES -> {
        int len = cardSize / 4;
        List<Card> cards = popStraight(minValue, 3, len);
        if (cards.isEmpty()) {
          yield cards;
        }
        for (int i = 0; i < len; i++) {
          cards = popOrRevert(cards, 1);
          if (cards.isEmpty()) {
            yield cards;
          }
        }
        yield cards;
      }
      case TRIPLE_STRAIGHT_WITH_PAIRS -> {
        int len = cardSize / 5;
        List<Card> cards = popStraight(minValue, 3, len);
        if (cards.isEmpty()) {
          yield cards;
        }
        for (int i = 0; i < len; i++) {
          cards = popOrRevert(cards, 2);
          if (cards.isEmpty()) {
            yield cards;
          }
        }
        yield cards;
      }
      case FOUR_WITH_TWO -> {
        List<Card> cards = popKind(minValue, 4, 4);
        if (cards.isEmpty()) {
          yield popBomb();
        }
        List<Card> singles = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
          singles = popOrRevert(singles, 1);
          if (singles.isEmpty()) {
            yield cards;
          }
        }
        cards.addAll(singles);
        yield cards;
      }
      case FOUR_WITH_PAIR -> {
        List<Card> cards = popKind(minValue, 4, 4);
        if (cards.isEmpty()) {
          yield popBomb();
        }
        List<Card> pair = popKind(0, 2, 3);
        if (!pair.isEmpty()) {
          cards.addAll(pair);
        }
        yield cards;
      }
      case FOUR_WITH_TWO_PAIRS -> {
        List<Card> cards = popKind(minValue, 4, 4);
        if (cards.isEmpty()) {
          yield popBomb();
        }
        List<Card> paris = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
          paris = popOrRevert(paris, 2);
          if (paris.isEmpty()) {
            yield cards;
          }
        }
        cards.addAll(paris);
        yield cards;
      }
      case FOUR_OF_KIND -> {
        List<Card> cards = popKind(minValue, 4, 4);
        yield cards.isEmpty() ? popRocket() : cards;
      }
      default -> Collections.emptyList();
    };
  }

  private List<Card> mayPopBomb() {
    boolean hasRocket = hasRocket();
    boolean hasBomb = kinds.values().stream().anyMatch(kind -> kind.size() == 4);
    if (!hasRocket && !hasBomb) {
      return Collections.emptyList();
    }
    List<Card> remaining = new ArrayList<>(20);
    for (CardsKind kind : kinds.values()) {
      if (kind.size() != 4
          && kind.getValue() != VALUE_OF_JOKER_1
          && kind.getValue() != VALUE_OF_JOKER_2) {
        remaining.addAll(kind.getCards());
      }
    }
    if (!remaining.isEmpty() && new CardsSet(remaining).getPokerHand() == null) {
      return Collections.emptyList();
    }
    return hasRocket ? popRocket() : popKind(0, 4, 4);
  }

  private List<Card> popBomb() {
    return hasRocket() ? popRocket() : popKind(0, 4, 4);
  }

  private boolean hasRocket() {
    return kinds.containsKey(VALUE_OF_JOKER_1) && kinds.containsKey(VALUE_OF_JOKER_2);
  }

  private List<Card> popRocket() {
    if (kinds.size() < 2) {
      return Collections.emptyList();
    }
    if (!hasRocket()) {
      return Collections.emptyList();
    }
    List<Card> cards = new ArrayList<>();
    cards.addAll(kinds.get(VALUE_OF_JOKER_1).pop(1));
    cards.addAll(kinds.get(VALUE_OF_JOKER_2).pop(1));
    return cards;
  }

  private List<Card> popOrRevert(List<Card> cards, int n) {
    List<Card> popped = popKind(0, n, 3);
    if (!popped.isEmpty()) {
      cards.addAll(popped);
      return cards;
    }
    for (Card card : cards) {
      kinds.get(card.getValue()).push(card);
    }
    return Collections.emptyList();
  }

  private List<Card> popKind(int minValue, int n, int maxN) {
    CardsKind[] indices = new CardsKind[] {null, null, null, null};
    for (CardsKind kind : kinds.values()) {
      if (kind.getValue() <= minValue || kind.size() < n || kind.size() > maxN) {
        continue;
      }
      if (kind.size() == n) {
        if (kind.getValue() == VALUE_OF_JOKER_1 && kinds.containsKey(VALUE_OF_JOKER_2)) {
          break;
        }
        return kind.pop(n);
      }
      if (indices[kind.size() - 1] == null) {
        indices[kind.size() - 1] = kind;
      }
    }
    for (int i = n; i < indices.length; i++) {
      if (indices[i] != null) {
        return indices[i].pop(n);
      }
    }
    return Collections.emptyList();
  }

  private List<Card> popMaxStraight(int minValue, int n, int minLen) {
    int fromValue = minValue + 1;
    for (int value = fromValue; value < VALUE_OF_RANK_2; value++) {
      CardsKind kind = kinds.get(value);
      if (kind == null || kind.size() < n) {
        if (value - fromValue >= minLen) {
          List<Card> values = new ArrayList<>(n * minLen);
          for (int i = fromValue; i < value; i++) {
            values.addAll(kinds.get(i).pop(n));
          }
          return values;
        }
        fromValue = value + 1;
      }
    }
    return Collections.emptyList();
  }

  private List<Card> popStraight(int minValue, int n, int len) {
    int fromValue = minValue + 1;
    for (int value = fromValue; value < VALUE_OF_RANK_2; value++) {
      CardsKind kind = kinds.get(value);
      if (kind == null || kind.size() < n) {
        fromValue = value + 1;
        continue;
      }
      if (value - fromValue + 1 < len) {
        continue;
      }
      List<Card> values = new ArrayList<>(n * len);
      for (int i = fromValue; i <= value; i++) {
        values.addAll(kinds.get(i).pop(n));
      }
      return values;
    }
    return Collections.emptyList();
  }
}
