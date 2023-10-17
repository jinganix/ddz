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

package io.github.jinganix.ddz.helper.uid;

import static io.github.jinganix.ddz.tests.TestConst.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Snowflake")
class SnowflakeTest {

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Nested
    @DisplayName("when datacenter id is -1")
    class WhenDataCenterIdIsMinus1 {

      @Test
      @DisplayName("then throw error")
      void thenThrow() {
        assertThatThrownBy(() -> new Snowflake(-1, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datacenterId can't be greater than 0 or less than 0");
      }
    }

    @Nested
    @DisplayName("when datacenter id is 1")
    class WhenDataCenterIdIs1 {

      @Test
      @DisplayName("then throw error")
      void thenThrow() {
        assertThatThrownBy(() -> new Snowflake(1, 0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("datacenterId can't be greater than 0 or less than 0");
      }
    }

    @Nested
    @DisplayName("when machine id is -1")
    class WhenDataMachineIdIsMinus1 {

      @Test
      @DisplayName("then throw error")
      void thenThrow() {
        assertThatThrownBy(() -> new Snowflake(0, -1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("machineId can't be greater than 1023 or less than 0");
      }
    }

    @Nested
    @DisplayName("when machine id is 1024")
    class WhenDataMachineIdIs1024 {

      @Test
      @DisplayName("then throw error")
      void thenThrow() {
        assertThatThrownBy(() -> new Snowflake(0, 1024))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("machineId can't be greater than 1023 or less than 0");
      }
    }
  }

  @Nested
  @DisplayName("nextId")
  class NextId {

    @Nested
    @DisplayName("when clock moves backwards")
    class WhenClockMovesBackwards {

      @Test
      @DisplayName("then throw error")
      void thenThrow() {
        Snowflake snowflake = new Snowflake(0, 0);
        snowflake.setClock(() -> MILLIS);
        snowflake.nextId();
        snowflake.setClock(() -> MILLIS - 1);
        assertThatThrownBy(snowflake::nextId)
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Clock moved backwards. Refusing to generate id");
      }
    }

    @Nested
    @DisplayName("when clock in same millis")
    class WhenClockInSameMillis {

      @Test
      @DisplayName("then generate sequential")
      void thenGenerateSequential() {
        Snowflake snowflake = new Snowflake(0, 0);
        snowflake.setClock(() -> MILLIS);
        long id = snowflake.nextId();
        assertThat(snowflake.nextId()).isEqualTo(id + 1);
      }
    }

    @Nested
    @DisplayName("when generate 4096 in milli")
    class WhenGenerate4096InMilli {

      @Test
      @DisplayName("then generate")
      void thenGenerate() {
        Snowflake snowflake = new Snowflake(0, 0);
        snowflake.setClock(() -> MILLIS);
        for (int i = 0; i < 4095; i++) {
          snowflake.nextId();
        }
        assertThat(snowflake.nextId()).isEqualTo(-6617288590328069613L);
      }
    }

    @Nested
    @DisplayName("when generate 4097 in milli")
    class WhenGenerate4097InMilli {

      @Test
      @DisplayName("then wait for next millis")
      void thenWaitForNextMillis() {
        Snowflake snowflake = new Snowflake(0, 0);
        snowflake.setClock(
            new Clock() {
              int counter = 0;

              @Override
              public long currentTimeMillis() {
                return counter++ > 4196 ? MILLIS + 1 : MILLIS;
              }
            });
        for (int i = 0; i < 4096; i++) {
          snowflake.nextId();
        }
        assertThat(snowflake.nextId()).isEqualTo(-6617288590323875308L);
      }
    }
  }
}
