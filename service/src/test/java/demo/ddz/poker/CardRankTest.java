package demo.ddz.poker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("CardRank")
class CardRankTest {

  static class EnumArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
        Arguments.of(1, CardRank.ACE),
        Arguments.of(2, CardRank.RANK_2),
        Arguments.of(3, CardRank.RANK_3),
        Arguments.of(4, CardRank.RANK_4),
        Arguments.of(5, CardRank.RANK_5),
        Arguments.of(6, CardRank.RANK_6),
        Arguments.of(7, CardRank.RANK_7),
        Arguments.of(8, CardRank.RANK_8),
        Arguments.of(9, CardRank.RANK_9),
        Arguments.of(10, CardRank.RANK_10),
        Arguments.of(11, CardRank.JACK),
        Arguments.of(12, CardRank.QUEEN),
        Arguments.of(13, CardRank.KING),
        Arguments.of(14, CardRank.JOKER_1),
        Arguments.of(15, CardRank.JOKER_2)
      );
    }
  }

  @Nested
  @DisplayName("fromValue")
  class FromValue {

    @Nested
    @DisplayName("when enum values is provided")
    class WhenEnumValuesIsProvided {

      @ParameterizedTest
      @DisplayName("then return expected enum")
      @ArgumentsSource(EnumArgumentsProvider.class)
      void thenReturnExpectedEnum(int value, CardRank expected) {
        assertEquals(expected, CardRank.fromValue(value));
      }
    }
  }

  @Nested
  @DisplayName("toValue")
  class ToValue {

    @Nested
    @DisplayName("when enums is provided")
    class WhenEnumsIsProvided {

      @ParameterizedTest
      @DisplayName("then return expected value")
      @ArgumentsSource(EnumArgumentsProvider.class)
      void thenReturnExpectedValue(int expected, CardRank cardRank) {
        assertEquals(expected, cardRank.getValue());
      }
    }
  }
}
