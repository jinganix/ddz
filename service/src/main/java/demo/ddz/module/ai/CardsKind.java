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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class CardsKind implements Comparable<CardsKind> {

  private final List<Card> cards;

  private final int value;

  public CardsKind(int value, List<Card> cards) {
    this.value = value;
    Collections.shuffle(cards);
    this.cards = cards;
  }

  @Override
  public int compareTo(CardsKind o) {
    return Integer.compare(value, o.getValue());
  }

  public int size() {
    return cards.size();
  }

  public void push(Card card) {
    this.cards.add(card);
  }

  public List<Card> pop(int n) {
    List<Card> values = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      values.add(cards.remove(cards.size() - 1));
    }
    return values;
  }
}
