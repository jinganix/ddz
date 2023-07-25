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

  private static final Map<CardRank, List<Card>> CARDS_MAP = new HashMap<>();

  private static final Map<String, CardRank> RANK_MAP = new HashMap<>();

  @BeforeAll
  static void initData() {
    for (int id = 1; id <= 54; id++) {
      Card card = new Card(id);
      List<Card> cards = CARDS_MAP.computeIfAbsent(card.getRank(), k -> new LinkedList<>());
      cards.add(card);
    }
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

  List<Card> toCards(String str) {
    String[] parts = str.split("");
    List<Card> cards = new ArrayList<>(str.length());
    for (String part : parts) {
      CardRank rank = RANK_MAP.get(part);
      List<Card> cardsOfRank = CARDS_MAP.get(rank);
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
            Arguments.of("3344556677889900JJQQ", PokerHand.DOUBLE_STRAIGHT),
            Arguments.of("AAAA", PokerHand.FOUR_OF_KIND),
            Arguments.of("AAAAXD", PokerHand.FOUR_WITH_TWO),
            Arguments.of("2AAAA2", PokerHand.FOUR_WITH_PAIR),
            Arguments.of("AAAA2233", PokerHand.FOUR_WITH_TWO_PAIRS),
            Arguments.of("77888899", PokerHand.FOUR_WITH_TWO_PAIRS),
            Arguments.of("AA", PokerHand.PAIR),
            Arguments.of("XD", PokerHand.ROCKET),
            Arguments.of("A", PokerHand.SINGLE),
            Arguments.of("34567", PokerHand.STRAIGHT),
            Arguments.of("34567890JQKA", PokerHand.STRAIGHT),
            Arguments.of("AAA", PokerHand.THREE_OF_KIND),
            Arguments.of("AAA99", PokerHand.THREE_WITH_PAIR),
            Arguments.of("9AAA9", PokerHand.THREE_WITH_PAIR),
            Arguments.of("AAAX", PokerHand.THREE_WITH_SINGLE),
            Arguments.of("2AAA", PokerHand.THREE_WITH_SINGLE),
            Arguments.of("333444555", PokerHand.TRIPLE_STRAIGHT),
            Arguments.of("333444555666", PokerHand.TRIPLE_STRAIGHT),
            Arguments.of("333444555778899", PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS),
            Arguments.of("33344455566677789JKA", PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES),
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
        "34",
        "3X",
        "3D",
        "A2",
        "AAAXD",
        "22223333",
        "AAAAX",
        "23456789JQK",
        "3456789JQKA2",
        "2233",
        "223344",
        "33344455578",
        "3334445557890",
        "3334445557788",
        "33344455577889900",
        "33344455566677788889",
        "234567890JQKAXD"
      })
      void thenReturnNullPokerHand(String input) {
        assertNull(new CardsSet(toCards(input)).getPokerHand());
      }
    }
  }
}
