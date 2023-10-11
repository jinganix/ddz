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

package demo.ddz.module.table;

import demo.ddz.module.poker.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

public class CardDeck {

  private static final int SIZE = 54;

  @Getter private final List<Card> cards = createCards();

  private int index = 0;

  private static List<Card> createCards() {
    ArrayList<Card> cards = new ArrayList<>(SIZE);
    for (int i = 1; i <= SIZE; i++) {
      cards.add(new Card(i));
    }
    return cards;
  }

  public void reset() {
    this.index = 0;
    Collections.shuffle(this.cards);
  }

  public int size() {
    return cards.size();
  }

  public Card pop() {
    return index >= size() ? null : cards.get(index++);
  }

  public Card peek() {
    return index >= size() ? null : cards.get(index);
  }
}
