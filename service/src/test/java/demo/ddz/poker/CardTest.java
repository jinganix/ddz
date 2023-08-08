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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("Card")
class CardTest {

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Nested
    @DisplayName("when card id is valid")
    class WhenCardIdIsValid {

      @ParameterizedTest
      @DisplayName("when card id is {0}")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenConstructTheCard(Integer cardId, CardSuit suit, CardRank rank, Integer value) {
        Card card = new Card(cardId);
        assertEquals(cardId, card.getId());
        assertEquals(suit, card.getSuit());
        assertEquals(rank, card.getRank());
        assertEquals(value, card.getValue());
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of(1, CardSuit.SPADES, CardRank.ACE, 14),
              Arguments.of(14, CardSuit.HEARTS, CardRank.ACE, 14),
              Arguments.of(27, CardSuit.CLUBS, CardRank.ACE, 14),
              Arguments.of(40, CardSuit.DIAMONDS, CardRank.ACE, 14),
              Arguments.of(2, CardSuit.SPADES, CardRank.RANK_2, 15),
              Arguments.of(41, CardSuit.DIAMONDS, CardRank.RANK_2, 15),
              Arguments.of(3, CardSuit.SPADES, CardRank.RANK_3, 3),
              Arguments.of(13, CardSuit.SPADES, CardRank.KING, 13),
              Arguments.of(52, CardSuit.DIAMONDS, CardRank.KING, 13),
              Arguments.of(53, CardSuit.JOKER, CardRank.JOKER_1, 16),
              Arguments.of(54, CardSuit.JOKER, CardRank.JOKER_2, 17));
        }
      }
    }

    @Nested
    @DisplayName("when card id is 0")
    class WhenCardIdIs0 {

      @Test
      @DisplayName("then throw exception")
      void thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(0));
      }
    }

    @Nested
    @DisplayName("when card id is 55")
    class WhenCardIdIs55 {

      @Test
      @DisplayName("then throw exception")
      void thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Card(55));
      }
    }
  }

  @Nested
  @DisplayName("compareTo")
  class CompareTo {

    @Nested
    @DisplayName("when cards pairs is provided")
    class WhenCardsPairIsProvided {

      @ParameterizedTest
      @DisplayName("then return expected compare result")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnExpectedCompareResult(int expected, Card a, Card b) {
        assertEquals(expected, a.compareTo(b));
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of(0, new Card(54), new Card(54)),
              Arguments.of(1, new Card(54), new Card(53)),
              Arguments.of(-1, new Card(53), new Card(54)),
              Arguments.of(1, new Card(14), new Card(1)),
              Arguments.of(-1, new Card(1), new Card(14)),
              Arguments.of(1, new Card(2), new Card(1)),
              Arguments.of(1, new Card(1), new Card(13)),
              Arguments.of(1, new Card(1), new Card(3)),
              Arguments.of(1, new Card(4), new Card(3)),
              Arguments.of(1, new Card(13), new Card(12)),
              Arguments.of(1, new Card(1), new Card(13)),
              Arguments.of(1, new Card(53), new Card(2)),
              Arguments.of(1, new Card(53), new Card(1)));
        }
      }
    }
  }
}
