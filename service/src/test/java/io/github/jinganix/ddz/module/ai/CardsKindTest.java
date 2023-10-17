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

package io.github.jinganix.ddz.module.ai;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.module.poker.Card;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CardsKind")
class CardsKindTest {

  @Nested
  @DisplayName("compareTo")
  class CompareTo {

    @Nested
    @DisplayName("when compare two kinds")
    class WhenCompareTwoKinds {

      @Test
      @DisplayName("then return result")
      void thenReturnResult() {
        CardsKind a = new CardsKind(5, new ArrayList<>(List.of(new Card(5))));
        CardsKind b = new CardsKind(6, new ArrayList<>(List.of(new Card(18))));
        assertThat(a.compareTo(b)).isEqualTo(-1);
      }
    }
  }

  @Nested
  @DisplayName("size")
  class Size {

    @Nested
    @DisplayName("when get size")
    class WhenPushACard {

      @Test
      @DisplayName("then return cards size")
      void thenReturnCardsSize() {
        CardsKind kind = new CardsKind(5, new ArrayList<>(List.of(new Card(5))));
        assertThat(kind.size()).isEqualTo(1);
      }
    }
  }

  @Nested
  @DisplayName("push")
  class Push {

    @Nested
    @DisplayName("when push a card")
    class WhenPushACard {

      @Test
      @DisplayName("then it is in tail")
      void thenItIsInTail() {
        CardsKind kind = new CardsKind(5, new ArrayList<>(List.of(new Card(5))));
        kind.push(new Card(18));
        assertThat(kind.getCards().get(0).getId()).isEqualTo(5);
        assertThat(kind.getCards().get(1).getId()).isEqualTo(18);
      }
    }
  }

  @Nested
  @DisplayName("pop")
  class Pop {

    @Nested
    @DisplayName("when pop 2 cards")
    class WhenPopTwoCards {

      @Test
      @DisplayName("then 2 cards are popped")
      void then2CardsArePopped() {
        CardsKind kind =
            new CardsKind(5, new ArrayList<>(List.of(new Card(5), new Card(18), new Card(31))));
        List<Card> cards = kind.pop(2);
        assertThat(cards).hasSize(2);
        assertThat(kind.getCards()).hasSize(1);
      }
    }
  }
}
