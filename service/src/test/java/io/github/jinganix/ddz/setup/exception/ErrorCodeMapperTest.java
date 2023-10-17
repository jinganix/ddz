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

package io.github.jinganix.ddz.setup.exception;

import io.github.jinganix.ddz.proto.error.ErrorCode;
import io.github.jinganix.ddz.tests.SpringIntegrationTests;
import java.util.Arrays;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("ErrorCodeMapper")
class ErrorCodeMapperTest extends SpringIntegrationTests {

  @Autowired ErrorCodeMapper errorCodeMapper;

  @Nested
  @DisplayName("mapErrorCode")
  class MapErrorCode {

    @Nested
    @DisplayName("when error code is null")
    class WhenErrorCodeIsNull {

      @Test
      @DisplayName("then return null")
      void thenReturnNull() {
        Assertions.assertThat(errorCodeMapper.mapErrorCode(null)).isNull();
      }
    }
  }

  @Nested
  @DisplayName("map")
  class Map {

    @Nested
    @DisplayName("when not instance of ErrorCode")
    class WhenNotInstanceOfErrorCode {

      @Test
      @DisplayName("then return ERROR")
      void thenReturn() {
        Assertions.assertThat(errorCodeMapper.map(Mockito.mock(ErrorCode.class)))
            .isEqualTo(ErrorCode.ERROR);
      }
    }

    @Nested
    @DisplayName("when is instance of ErrorCode")
    class WhenIsInstanceOfErrorCode {

      static class TestArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
          return Arrays.stream(io.github.jinganix.ddz.module.utils.ErrorCode.values())
              .map(e -> Arguments.of(e, ErrorCode.valueOf(e.name())));
        }
      }

      @ParameterizedTest(name = "map({0}) => {1}")
      @DisplayName("then return expected enum")
      @ArgumentsSource(TestArgumentsProvider.class)
      void thenReturnExpectedEnum(
          io.github.jinganix.ddz.module.utils.ErrorCode value, ErrorCode expected) {
        Assertions.assertThat(errorCodeMapper.map(value)).isEqualTo(expected);
      }
    }
  }
}
