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

package io.github.jinganix.ddz.module.table;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jinganix.ddz.module.poker.Card;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CardDeck")
class CardDeckTest {

  @Nested
  @DisplayName("reset")
  class Reset {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then reset")
      void thenReset() {
        CardDeck deck = new CardDeck();
        List<Card> cards = new ArrayList<>(deck.getCards());
        deck.reset();
        assertThat(deck.getCards()).containsExactlyInAnyOrderElementsOf(cards);
        assertThat(deck.getCards().equals(cards)).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("size")
  class Size {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return size")
      void thenReturn() {
        CardDeck deck = new CardDeck();
        assertThat(deck.size()).isEqualTo(54);
      }
    }
  }

  @Nested
  @DisplayName("peek")
  class Peek {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then peek card")
      void thenPeekCard() {
        CardDeck deck = new CardDeck();
        assertThat(deck.peek()).isNotNull();
        for (int i = 0; i < deck.size(); i++) {
          deck.peek();
        }
        assertThat(deck.peek()).isNotNull();
      }
    }

    @Nested
    @DisplayName("when peek tail")
    class WhenPeekTail {

      @Test
      @DisplayName("then return null")
      void thenReturnNull() {
        CardDeck deck = new CardDeck();
        for (int i = 0; i < deck.size(); i++) {
          deck.pop();
        }
        assertThat(deck.peek()).isNull();
      }
    }
  }

  @Nested
  @DisplayName("pop")
  class Pop {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then pop card")
      void thenPopCard() {
        CardDeck deck = new CardDeck();
        assertThat(deck.pop()).isNotNull();
        for (int i = 0; i < deck.size(); i++) {
          deck.pop();
        }
        assertThat(deck.pop()).isNull();
      }
    }
  }
}
