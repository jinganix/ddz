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

@DisplayName("CardSuit")
class CardSuitTest {

  static class EnumArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
        Arguments.of(1, CardSuit.SPADES),
        Arguments.of(2, CardSuit.HEARTS),
        Arguments.of(3, CardSuit.CLUBS),
        Arguments.of(4, CardSuit.DIAMONDS),
        Arguments.of(5, CardSuit.JOKER)
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
      void thenReturnExpectedEnum(int value, CardSuit expected) {
        assertEquals(expected, CardSuit.fromValue(value));
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
      void thenReturnExpectedValue(int expected, CardSuit cardSuit) {
        assertEquals(expected, cardSuit.getValue());
      }
    }
  }
}
