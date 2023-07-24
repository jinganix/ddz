package demo.ddz.poker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
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

  private static final Map<CardRank, List<Card>> cardsMap = new HashMap<>();

  @BeforeAll
  static void initCardsMap() {
    for (int id = 1; id <= 54; id++) {
      Card card = new Card(id);
      List<Card> cards = cardsMap.computeIfAbsent(card.getRank(), k -> new LinkedList<>());
      cards.add(card);
    }
  }

  List<Card> toCards(String str) {
    String[] parts = str.split("");
    List<Card> cards = new ArrayList<>(str.length());
    for (String part : parts) {
      CardRank rank = CardRank.fromValue(Integer.parseInt(part, 16));
      List<Card> cardsOfRank = cardsMap.get(rank);
      cards.add(cardsOfRank.get(0));
    }
    return cards;
  }

  @Nested
  @DisplayName("getPokerHand")
  class GetPokerHand {

    @Disabled("Disable until implemented")
    @Nested
    @DisplayName("when a valid cards set is provided")
    class WhenCardsSetIsValid {

      @ParameterizedTest(name = "{0} => {1}")
      @DisplayName("then return the poker hand")
      @ArgumentsSource(PokerHandArgumentsProvider.class)
      void thenReturnThePokerHand(String input, PokerHand expected) {
        assertEquals(expected, new CardsSet(toCards(input)).getPokerHand());
      }

      static class PokerHandArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
            Arguments.of("334455", PokerHand.DOUBLE_STRAIGHT),
            Arguments.of("33445566778899AABBCC", PokerHand.DOUBLE_STRAIGHT),
            Arguments.of("1111", PokerHand.FOUR_OF_KIND),
            Arguments.of("1111EF", PokerHand.FOUR_WITH_TWO),
            Arguments.of("211112", PokerHand.FOUR_WITH_PAIR),
            Arguments.of("11112233", PokerHand.FOUR_WITH_TOW_PAIRS),
            Arguments.of("11", PokerHand.PAIR),
            Arguments.of("EF", PokerHand.ROCKET),
            Arguments.of("1", PokerHand.SINGLE),
            Arguments.of("34567", PokerHand.STRAIGHT),
            Arguments.of("3456789ABCD1", PokerHand.STRAIGHT),
            Arguments.of("111", PokerHand.THREE_OF_KIND),
            Arguments.of("11199", PokerHand.THREE_WITH_PAIR),
            Arguments.of("91119", PokerHand.THREE_WITH_PAIR),
            Arguments.of("111E", PokerHand.THREE_WITH_SINGLE),
            Arguments.of("2111", PokerHand.THREE_WITH_SINGLE),
            Arguments.of("333444555", PokerHand.TRIPLE_STRAIGHT),
            Arguments.of("333444555666", PokerHand.TRIPLE_STRAIGHT),
            Arguments.of("333444555778899", PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS),
            Arguments.of("33344455566677789ABC", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES),
            Arguments.of("333444555777", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES),
            Arguments.of("333444555778", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES),
            Arguments.of("333444555789", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES)
          );
        }
      }
    }

    @Nested
    @DisplayName("when a invalid cards set is provided")
    class WhenCardsSetIsInvalid {

      @ParameterizedTest(name = "{0} => null")
      @DisplayName("then return null poker hand")
      @ValueSource(strings = {
        "12",
        "111EF",
        "1111EF",
        "11112222",
        "1111E",
        "23456789ABCD",
        "3456789ABCD12",
        "2233",
        "223344",
        "333444555789A",
        "3334445557788",
        "333444555778899AA",
        "33344455566677788889"
      })
      void thenReturnNullPokerHand(String input) {
        assertNull(new CardsSet(toCards(input)).getPokerHand());
      }
    }
  }
}
