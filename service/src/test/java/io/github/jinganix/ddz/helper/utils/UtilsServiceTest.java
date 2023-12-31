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

package io.github.jinganix.ddz.helper.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.byLessThan;

import io.github.jinganix.ddz.tests.SpringIntegrationTests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("UtilsService")
class UtilsServiceTest extends SpringIntegrationTests {

  @Autowired UtilsService utilsService;

  @Nested
  @DisplayName("currentTimeMillis")
  class CurrentTimeMillis {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return current time millis")
      void thenReturnCurrentTimeMillis() {
        assertThat(utilsService.currentTimeMillis())
            .isCloseTo(System.currentTimeMillis(), byLessThan(1000L));
      }
    }
  }

  @Nested
  @DisplayName("nextInt(int origin, int bound)")
  class NextInt {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then return rand value")
      void thenReturnRandValue() {
        assertThat(utilsService.nextInt(10, 11)).isEqualTo(10);
      }
    }
  }

  @Nested
  @DisplayName("uuid")
  class Uuid {

    @Nested
    @DisplayName("when dash is true")
    class WhenDashIsTrue {

      @Test
      @DisplayName("then return dashed uuid")
      void thenReturn() {
        assertThat(utilsService.uuid(true)).hasSize(36);
      }
    }

    @Nested
    @DisplayName("when dash is false")
    class WhenDashIsFalse {

      @Test
      @DisplayName("then return non dashed uuid")
      void thenReturn() {
        assertThat(utilsService.uuid(false)).hasSize(32);
      }
    }
  }
}
