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

package demo.ddz.tests;

import demo.ddz.module.poker.Card;
import demo.ddz.module.poker.CardRank;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CardsHelper {

  private static final Map<String, CardRank> RANK_MAP = new HashMap<>();

  static {
    RANK_MAP.put("2", CardRank.RANK_2);
    RANK_MAP.put("3", CardRank.RANK_3);
    RANK_MAP.put("4", CardRank.RANK_4);
    RANK_MAP.put("5", CardRank.RANK_5);
    RANK_MAP.put("6", CardRank.RANK_6);
    RANK_MAP.put("7", CardRank.RANK_7);
    RANK_MAP.put("8", CardRank.RANK_8);
    RANK_MAP.put("9", CardRank.RANK_9);
    RANK_MAP.put("0", CardRank.RANK_10);
    RANK_MAP.put("J", CardRank.JACK);
    RANK_MAP.put("Q", CardRank.QUEEN);
    RANK_MAP.put("K", CardRank.KING);
    RANK_MAP.put("A", CardRank.ACE);
    RANK_MAP.put("X", CardRank.JOKER_1);
    RANK_MAP.put("D", CardRank.JOKER_2);
  }

  private final Map<CardRank, List<Card>> cardsMap = new HashMap<>();

  public CardsHelper() {}

  public CardsHelper(List<Card> cards) {
    for (Card card : cards) {
      cardsMap.computeIfAbsent(card.getRank(), k -> new LinkedList<>()).add(card);
    }
  }

  public void initialize() {
    cardsMap.clear();
    for (int id = 1; id <= 54; id++) {
      Card card = new Card(id);
      cardsMap.computeIfAbsent(card.getRank(), k -> new LinkedList<>()).add(card);
    }
  }

  public List<Card> toCards(String str) {
    String[] parts = str.split("");
    List<Card> cards = new ArrayList<>(str.length());
    for (String part : parts) {
      CardRank rank = RANK_MAP.get(part);
      List<Card> cardsOfRank = cardsMap.get(rank);
      cards.add(cardsOfRank.remove(0));
    }
    return cards;
  }
}
