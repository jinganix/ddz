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

package demo.ddz.module.ai;

import static org.assertj.core.api.Assertions.assertThat;

import demo.ddz.module.poker.Card;
import demo.ddz.module.poker.CardsSet;
import demo.ddz.tests.CardsHelper;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("ShallowMind")
class ShallowMindTest {

  final CardsHelper cardsHelper = new CardsHelper();

  @BeforeEach
  void beforeEach() {
    cardsHelper.initialize();
  }

  List<Card> toCards(String str) {
    return cardsHelper.toCards(str);
  }

  @Nested
  @DisplayName("isEmpty")
  class IsEmpty {

    @Nested
    @DisplayName("when cards is empty")
    class WhenCardsIsEmpty {

      @Test
      @DisplayName("then return true")
      void thenReturn() {
        ShallowMind mind = new ShallowMind(Collections.emptyList());
        assertThat(mind.isEmpty()).isTrue();
      }
    }

    @Nested
    @DisplayName("when card kind is empty")
    class WhenCardKindIsEmpty {

      @Test
      @DisplayName("then return true")
      void thenReturn() {
        ShallowMind mind = new ShallowMind(List.of(new Card(1)));
        mind.followSuit(new CardsSet(Lists.newArrayList(new Card(3))));
        assertThat(mind.isEmpty()).isTrue();
      }
    }

    @Nested
    @DisplayName("when cards is not empty")
    class WhenCardsIsNotEmpty {

      @Test
      @DisplayName("then return false")
      void thenReturn() {
        ShallowMind mind = new ShallowMind(List.of(new Card(1)));
        assertThat(mind.isEmpty()).isFalse();
      }
    }
  }

  @Nested
  @DisplayName("toCards")
  class ToCards {

    @Nested
    @DisplayName("when cards is empty")
    class WhenCardsIsEmpty {

      @Test
      @DisplayName("then return empty cards")
      void thenReturn() {
        ShallowMind mind = new ShallowMind(Collections.emptyList());
        assertThat(mind.toCards()).isEmpty();
      }
    }

    @Nested
    @DisplayName("when cards is not empty")
    class WhenCardsIsNotEmpty {

      @Test
      @DisplayName("then return false")
      void thenReturn() {
        List<Card> cards = List.of(new Card(1), new Card(2));
        ShallowMind mind = new ShallowMind(cards);
        assertThat(mind.toCards())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(cards);
      }
    }
  }

  @Nested
  @DisplayName("followSuit")
  class FollowSuit {

    @Nested
    @DisplayName("when there are dominating cards")
    class WhenThereAreDominatingCards {

      @ParameterizedTest(name = "{0} => {2} in {1}")
      @DisplayName("then return cards")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnCards(String cardsStr, String handCardsStr, String expected) {
        CardsSet cardsSet = new CardsSet(toCards(cardsStr));
        List<Card> handCards = toCards(handCardsStr);
        ShallowMind mind = new ShallowMind(handCards);
        assertThat(mind.followSuit(cardsSet).stream().map(Card::getValue).sorted().toList())
            .isEqualTo(
                new CardsHelper(handCards)
                    .toCards(expected).stream().map(Card::getValue).sorted().toList());
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of("3", "345", "4"),
              Arguments.of("3", "44", "4"),
              Arguments.of("4", "3445", "5"),
              Arguments.of("4", "5566", "5"),
              Arguments.of("J", "44445", "4444"),
              Arguments.of("J", "5X", "X"),
              Arguments.of("J", "5XD", "XD"),
              Arguments.of("33", "3445", "44"),
              Arguments.of("33", "444", "44"),
              Arguments.of("333", "34445", "444"),
              Arguments.of("333", "34444", "4444"),
              Arguments.of("333", "5XD", "XD"),
              Arguments.of("3334", "56667", "6665"),
              Arguments.of("3334", "56666", "6666"),
              Arguments.of("3334", "5XD", "XD"),
              Arguments.of("33388", "344455", "44455"),
              Arguments.of("33388", "444455", "4444"),
              Arguments.of("33388", "5XD", "XD"),
              Arguments.of("3333", "4444567", "4444"),
              Arguments.of("34567", "8888", "8888"),
              Arguments.of("34567", "4567890JQKA", "45678"),
              Arguments.of("34567", "4556789", "45678"),
              Arguments.of("34567", "45556789", "45678"),
              Arguments.of("34567", "5XD", "XD"),
              Arguments.of("45678", "345679999", "9999"),
              Arguments.of("334455", "34455666777", "445566"),
              Arguments.of("334455", "77778", "7777"),
              Arguments.of("334455", "5XD", "XD"),
              Arguments.of("333444555", "567778889990JJ", "777888999"),
              Arguments.of("333444555", "77778", "7777"),
              Arguments.of("333444555789", "567778889990JQKA2", "777888999560"),
              Arguments.of("3334445566", "67778889900JQ", "7778889900"),
              Arguments.of("3334445566", "5XD", "XD"),
              Arguments.of("444456", "5777789", "777758"),
              Arguments.of("444456", "7777", "7777"),
              Arguments.of("444456", "567XD", "XD"),
              Arguments.of("444455", "3333", "3333"),
              Arguments.of("444455", "3333XD", "XD"),
              Arguments.of("444455", "3333X", "3333"),
              Arguments.of("444455", "3333D", "3333"),
              Arguments.of("444455", "6666XD", "6666"),
              Arguments.of("444455", "5777788", "777788"),
              Arguments.of("444455", "7777", "7777"),
              Arguments.of("444455", "567XD", "XD"),
              Arguments.of("444455", "77778", "7777"),
              Arguments.of("44445566", "55777788", "77775588"),
              Arguments.of("44445566", "77778", "7777"),
              Arguments.of("44445566", "7777", "7777"),
              Arguments.of("44445566", "567XD", "XD"),
              Arguments.of("4444", "55777788", "7777"),
              Arguments.of("4444", "567XD", "XD"));
        }
      }
    }

    @Nested
    @DisplayName("when there are no dominating cards")
    class WhenThereAreNoDominatingCards {

      @ParameterizedTest(name = "{0} => empty in {1}")
      @DisplayName("then return empty")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnEmpty(String cardsStr, String handCardsStr) {
        CardsSet cardsSet = new CardsSet(toCards(cardsStr));
        List<Card> handCards = toCards(handCardsStr);
        ShallowMind mind = new ShallowMind(handCards);
        assertThat(mind.followSuit(cardsSet)).isEmpty();
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of("J", "3"),
              Arguments.of("J", "444456"),
              Arguments.of("J", "56XD"),
              Arguments.of("33", "567"),
              Arguments.of("333", "56XD"),
              Arguments.of("3334", "5667"),
              Arguments.of("3334", "56XD"),
              Arguments.of("55588", "344466"),
              Arguments.of("5555", "4444678"),
              Arguments.of("45678", "3456790J"),
              Arguments.of("45678", "JQKA2"),
              Arguments.of("45678", "KA2XD"),
              Arguments.of("45678", "56XD"),
              Arguments.of("778899", "334455678"),
              Arguments.of("778899", "56XD"),
              Arguments.of("666777888", "333444555678"),
              Arguments.of("666777888", "56XD"),
              Arguments.of("666777888JQK", "333444555678"),
              Arguments.of("66677789", "888999J"),
              Arguments.of("66677789", "7890"),
              Arguments.of("666777888JQK", "56XD"),
              Arguments.of("6667778899", "5678"),
              Arguments.of("3334445566", "888999JJ"),
              Arguments.of("6667778899", "333444JJQQ"),
              Arguments.of("6667778899", "56XD"),
              Arguments.of("4444", "X"),
              Arguments.of("XD", "3333"));
        }
      }
    }
  }

  @Nested
  @DisplayName("leadOff")
  class LeadOff {

    @Nested
    @DisplayName("when leads off")
    class WhenLeadsOff {

      @ParameterizedTest(name = "{0} => {1}")
      @DisplayName("then return cards")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnCards(String handCardsStr, String expected) {
        List<Card> handCards = toCards(handCardsStr);
        ShallowMind mind = new ShallowMind(handCards);
        assertThat(mind.leadOff().stream().map(Card::getValue).sorted().toList())
            .isEqualTo(
                new CardsHelper(handCards)
                    .toCards(expected).stream().map(Card::getValue).sorted().toList());
        assertThat(mind.toCards().size()).isEqualTo(handCardsStr.length() - expected.length());
      }

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
              Arguments.of("666", "666"),
              Arguments.of("666888", "666"),
              Arguments.of("345666", "6663"),
              Arguments.of("44666", "66644"),
              Arguments.of("3456789JQK", "3456789"),
              Arguments.of("344556689", "445566"),
              Arguments.of("3455556", "3"),
              Arguments.of("446677", "44"));
        }
      }
    }
  }
}
