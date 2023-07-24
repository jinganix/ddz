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

@DisplayName("PokerHand")
class PokerHandTest {

  static class EnumArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
        Arguments.of(1, PokerHand.SINGLE),
        Arguments.of(2, PokerHand.PAIR),
        Arguments.of(3, PokerHand.THREE_OF_KIND),
        Arguments.of(4, PokerHand.THREE_WITH_SINGLE),
        Arguments.of(5, PokerHand.THREE_WITH_PAIR),
        Arguments.of(6, PokerHand.STRAIGHT),
        Arguments.of(7, PokerHand.DOUBLE_STRAIGHT),
        Arguments.of(8, PokerHand.TRIPLE_STRAIGHT),
        Arguments.of(9, PokerHand.TRIPLE_STRAIGHT_WITH_SINGLES),
        Arguments.of(10, PokerHand.TRIPLE_STRAIGHT_WITH_PAIRS),
        Arguments.of(11, PokerHand.FOUR_WITH_TWO),
        Arguments.of(12, PokerHand.FOUR_WITH_PAIR),
        Arguments.of(13, PokerHand.FOUR_WITH_TOW_PAIRS),
        Arguments.of(14, PokerHand.FOUR_OF_KIND),
        Arguments.of(15, PokerHand.ROCKET)
      );
    }
  }

  @Nested
  @DisplayName("fromValue")
  class FromValue {

    @Nested
    @DisplayName("when enum values is provided")
    class WhenEnumValuesIsProvided {

      @ParameterizedTest(name = "fromValue({0}) => {1}")
      @DisplayName("then return expected enum")
      @ArgumentsSource(EnumArgumentsProvider.class)
      void thenReturnExpectedEnum(int value, PokerHand expected) {
        assertEquals(expected, PokerHand.fromValue(value));
      }
    }
  }

  @Nested
  @DisplayName("toValue")
  class ToValue {

    @Nested
    @DisplayName("when enums is provided")
    class WhenEnumsIsProvided {

      @ParameterizedTest(name = "{1}.toValue() => {0}")
      @DisplayName("then return expected value")
      @ArgumentsSource(EnumArgumentsProvider.class)
      void thenReturnExpectedValue(int expected, PokerHand pokerHand) {
        assertEquals(expected, pokerHand.getValue());
      }
    }
  }
}
