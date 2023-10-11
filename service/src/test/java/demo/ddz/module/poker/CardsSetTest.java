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

package demo.ddz.module.poker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import demo.ddz.tests.CardsHelper;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("CardsSet")
class CardsSetTest {

  final CardsHelper cardsHelper = new CardsHelper();

  @BeforeEach
  void beforeEach() {
    cardsHelper.initialize();
  }

  List<Card> toCards(String str) {
    return cardsHelper.toCards(str);
  }

  @Nested
  @DisplayName("getPokerHand")
  class GetPokerHand {

    @Nested
    @DisplayName("when a valid cards set is provided")
    class WhenCardsSetIsValid {

      @ParameterizedTest(name = "{0} => {1}")
      @DisplayName("then return the poker hand")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnThePokerHand(PokerHand expected, String input) {
        assertEquals(expected, new CardsSet(toCards(input)).getPokerHand());
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of(PokerHand.DOUBLE_STRAIGHT, "334455"),
              Arguments.of(PokerHand.DOUBLE_STRAIGHT, "3344556677889900JJQQ"),
              Arguments.of(PokerHand.FOUR_OF_KIND, "AAAA"),
              Arguments.of(PokerHand.FOUR_WITH_PAIR, "AAAA22"),
              Arguments.of(PokerHand.FOUR_WITH_PAIR, "JJAAAA"),
              Arguments.of(PokerHand.FOUR_WITH_TWO, "AAAAXD"),
              Arguments.of(PokerHand.FOUR_WITH_TWO, "JAAAAX"),
              Arguments.of(PokerHand.FOUR_WITH_TWO, "JQAAAA"),
              Arguments.of(PokerHand.FOUR_WITH_TWO_PAIRS, "66778888"),
              Arguments.of(PokerHand.FOUR_WITH_TWO_PAIRS, "77888899"),
              Arguments.of(PokerHand.FOUR_WITH_TWO_PAIRS, "88889999"),
              Arguments.of(PokerHand.FOUR_WITH_TWO_PAIRS, "8888JJQQ"),
              Arguments.of(PokerHand.PAIR, "AA"),
              Arguments.of(PokerHand.ROCKET, "XD"),
              Arguments.of(PokerHand.SINGLE, "A"),
              Arguments.of(PokerHand.STRAIGHT, "34567"),
              Arguments.of(PokerHand.STRAIGHT, "34567890JQKA"),
              Arguments.of(PokerHand.THREE_OF_KIND, "AAA"),
              Arguments.of(PokerHand.THREE_WITH_PAIR, "99AAA"),
              Arguments.of(PokerHand.THREE_WITH_PAIR, "AAA22"),
              Arguments.of(PokerHand.THREE_WITH_SINGLE, "2AAA"),
              Arguments.of(PokerHand.THREE_WITH_SINGLE, "AAAX"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT, "333444555"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT, "333444555666777888"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "33334444666777888999"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "3333444555"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444555666JJJJQQQQ"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444555778899"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444JJJJ"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "3355566688"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333344445555"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33334444555566667777"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3333444455556666777J"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3334444555566667777J"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444455559"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33344455"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3334445556666JJJ"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33344455566677789JKA"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555666777JJJJQ"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555666JJJJ"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555777"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555778"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555789"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333555666777"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "3335556667778888"),
              Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "QQQKKKAAA222"));
        }
      }
    }

    @Nested
    @DisplayName("when a invalid cards set is provided")
    class WhenCardsSetIsInvalid {

      @ParameterizedTest(name = "{0} => null")
      @DisplayName("then return null poker hand")
      @ValueSource(
          strings = {
            "2233",
            "223344",
            "234567890JQKAXD",
            "23456789JQK",
            "3334445557788",
            "33344455577889900",
            "33344455578",
            "3334445557890",
            "333666777",
            "333666777999",
            "333666777JJQQKK",
            "34",
            "3456789JQKA2",
            "3D",
            "3X",
            "A2",
            "AAA22257",
            "AAAAX",
            "AAAXD",
            "JQKA2",
            "QQKKAA22"
          })
      void thenReturnNullPokerHand(String input) {
        assertNull(new CardsSet(toCards(input)).getPokerHand());
      }
    }
  }

  @Nested
  @DisplayName("dominate")
  class Dominate {

    @Nested
    @DisplayName("when this cards set is dominating")
    class WhenThisCardsSetIsDominating {

      @ParameterizedTest(name = "{0} > {1}")
      @DisplayName("then return true")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnTrue(String a, String b) {
        assertTrue(new CardsSet(toCards(a)).dominate(new CardsSet(toCards(b))));
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of("3333", "0JQKA"),
              Arguments.of("3333", "22"),
              Arguments.of("3333", "222"),
              Arguments.of("3333", "222A"),
              Arguments.of("3333", "222AA"),
              Arguments.of("3333", "44445555"),
              Arguments.of("3333", "444555"),
              Arguments.of("3333", "44455566"),
              Arguments.of("3333", "4445556666"),
              Arguments.of("3333", "4445556677"),
              Arguments.of("3333", "D"),
              Arguments.of("3333", "QQKKAA"),
              Arguments.of("3333", "QQQKKKAAA"),
              Arguments.of("4", "3"),
              Arguments.of("44", "33"),
              Arguments.of("444", "333"),
              Arguments.of("4444", "3333"),
              Arguments.of("4445", "3336"),
              Arguments.of("44455", "33366"),
              Arguments.of("445566", "334455"),
              Arguments.of("45678", "34567"),
              Arguments.of("55558888", "66667777"),
              Arguments.of("666677", "4444JJ"),
              Arguments.of("66667788", "4444JJQQ"),
              Arguments.of("666678", "4444JQ"),
              Arguments.of("66668888", "4444JJQQ"),
              Arguments.of("666777888", "333444555"),
              Arguments.of("6667778888", "444555JJQQ"),
              Arguments.of("666777888999", "333444555JJJ"),
              Arguments.of("6667778899", "444555JJQQ"),
              Arguments.of("XD", "3333"));
        }
      }
    }

    @Nested
    @DisplayName("when this cards set is not dominating")
    class WhenThisCardsSetIsNotDominating {

      @ParameterizedTest(name = "{0} <= {1}")
      @DisplayName("then return false")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnFalse(String a, String b) {
        assertFalse(new CardsSet(toCards(a)).dominate(new CardsSet(toCards(b))));
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of("3", "4"),
              Arguments.of("33", "33"),
              Arguments.of("33", "4"),
              Arguments.of("333", "4"),
              Arguments.of("333444", "4"),
              Arguments.of("333444555666", "777888999JJJ"),
              Arguments.of("3334446666", "4"),
              Arguments.of("334455", "4"),
              Arguments.of("34567", "4"),
              Arguments.of("34567", "AAAA"),
              Arguments.of("3579J", "4"),
              Arguments.of("4", "4"),
              Arguments.of("555522", "4"),
              Arguments.of("55552233", "4"),
              Arguments.of("55556666", "4"),
              Arguments.of("AAAA", "XD"));
        }
      }
    }
  }
}
