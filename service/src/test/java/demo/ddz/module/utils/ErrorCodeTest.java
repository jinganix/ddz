/*
 * Copyright (c) 2020 https://github.com/jinganix/ddz, All Rights Reserved.
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

package demo.ddz.module.utils;

import static org.assertj.core.api.Assertions.assertThat;

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
          Arguments.of(0, ErrorCode.OK),
          Arguments.of(1, ErrorCode.TABLE_NOT_FOUND),
          Arguments.of(2, ErrorCode.PHASE_INVALID),
          Arguments.of(3, ErrorCode.NOT_CURRENT_PLAYER),
          Arguments.of(4, ErrorCode.INVALID_PLAYED_CARDS),
          Arguments.of(5, ErrorCode.PLAYED_CARDS_NOT_DOMINATING),
          Arguments.of(6, ErrorCode.INVALID_PLAYER_STATE),
          Arguments.of(7, ErrorCode.PLAYER_IS_OFFLINE));
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
        assertThat(ErrorCode.fromValue(value)).isEqualTo(expected);
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
        assertThat(errorCode.getValue()).isEqualTo(expected);
      }
    }
  }
}
