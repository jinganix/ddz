package demo.ddz.poker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

@Disabled("Disable until implemented")
@DisplayName("CardsSet")
class CardsSetTest {

  private Map<CardRank, List<Card>> cardsMap = new HashMap<>();

  public CardsSetTest() {
    for (int id = 1; id < 54; id++) {
      Card card = new Card(id);
      List<Card> cards = cardsMap.computeIfAbsent(card.getRank(), k -> new LinkedList<>());
      cards.add(card);
    }
  }

  List<Card> toCards(String str) {
    String[] parts = str.split("");
    List<Card> cards = new ArrayList<>(str.length());
    for (String part : parts) {
      CardRank rank = CardRank.values()[Integer.parseInt(part, 16) - 1];
      List<Card> cardsOfRank = cardsMap.get(rank);
      cards.add(cardsOfRank.remove(0));
    }
    return cards;
  }

  @Nested
  @DisplayName("getPokerHand")
  class GetPokerHandTest {

    @Nested
    @DisplayName("when a valid cards set is provided")
    class WhenCardsSetIsValid {

      @ParameterizedTest
      @DisplayName("then return the poker hand")
      @ArgumentsSource(BlankStringsArgumentsProvider.class)
      void thenReturnThePokerHandTest(PokerHand expected, String input) {
        assertEquals(expected, new CardsSet(toCards(input)).getPokerHand());
      }

      static class BlankStringsArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Stream.of(
            Arguments.of(PokerHand.DOUBLE_STRAIGHT, "334455"),
            Arguments.of(PokerHand.DOUBLE_STRAIGHT, "33445566778899AABBCC"),
            Arguments.of(PokerHand.FOUR_OF_KIND, "1111"),
            Arguments.of(PokerHand.FOUR_WITH_TWO, "1111EF"),
            Arguments.of(PokerHand.FOUR_WITH_PAIR, "211112"),
            Arguments.of(PokerHand.FOUR_WITH_TOW_PAIRS, "11112233"),
            Arguments.of(PokerHand.PAIR, "11"),
            Arguments.of(PokerHand.ROCKET, "EF"),
            Arguments.of(PokerHand.SINGLE, "1"),
            Arguments.of(PokerHand.STRAIGHT, "34567"),
            Arguments.of(PokerHand.STRAIGHT, "3456789ABCD1"),
            Arguments.of(PokerHand.THREE_OF_KIND, "111"),
            Arguments.of(PokerHand.THREE_WITH_PAIR, "11199"),
            Arguments.of(PokerHand.THREE_WITH_PAIR, "91119"),
            Arguments.of(PokerHand.THREE_WITH_SINGLE, "111E"),
            Arguments.of(PokerHand.THREE_WITH_SINGLE, "2111"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT, "333444555"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT, "333444555666"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS, "333444555778899"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "33344455566677789ABC"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555777"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555778"),
            Arguments.of(PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES, "333444555789")
          );
        }
      }
    }

    @Nested
    @DisplayName("when a invalid cards set is provided")
    class WhenCardsSetIsInvalid {

      @ParameterizedTest
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
      void thenReturnNullPokerHandTest(String input) {
        assertNull(new CardsSet(toCards(input)).getPokerHand());
      }
    }
  }
}
