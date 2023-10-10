package demo.ddz.module.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

@DisplayName("ErrorCode")
class ErrorCodeTest {

  static class TestArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(1, ErrorCode.TABLE_NOT_FOUND),
          Arguments.of(2, ErrorCode.PHASE_INVALID),
          Arguments.of(3, ErrorCode.NOT_CURRENT_PLAYER),
          Arguments.of(4, ErrorCode.INVALID_PLAYED_CARDS),
          Arguments.of(5, ErrorCode.CARDS_SET_NOT_DOMINATED));
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
      @ArgumentsSource(ErrorCodeTest.TestArgumentsProvider.class)
      void thenReturnExpectedEnum(int value, ErrorCode expected) {
        assertEquals(expected, ErrorCode.fromValue(value));
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
      @ArgumentsSource(ErrorCodeTest.TestArgumentsProvider.class)
      void thenReturnExpectedValue(int expected, ErrorCode errorCode) {
        assertEquals(expected, errorCode.getValue());
      }
    }
  }
}
