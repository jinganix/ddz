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

package io.github.jinganix.ddz.helper.timer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MultiLevelWheelTimer")
class MultiLevelWheelTimerTest {

  MultiLevelWheelTimer timer;

  @AfterEach
  void cleanup() {
    timer.stop();
  }

  @Nested
  @DisplayName("when level is 1")
  class WhenLevelIs1 {

    @BeforeEach
    void beforeEach() {
      timer = new MultiLevelWheelTimer(10, 5, 1);
    }

    @Nested
    @DisplayName("when delay < wheel duration")
    class WhenDelayLessThanWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(20, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(20L, 120L));
      }
    }

    @Nested
    @DisplayName("when delay = wheel duration")
    class WhenDelayEqualWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(50, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(50L, 150L));
      }
    }

    @Nested
    @DisplayName("when delay > wheel duration x 2")
    class WhenDelayGreaterThanDoubleWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(120, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(120L, 220L));
      }
    }
  }

  @Nested
  @DisplayName("when level is 2")
  class WhenLevelIs2 {

    @BeforeEach
    void beforeEach() {
      timer = new MultiLevelWheelTimer(10, 5, 2);
    }

    @Nested
    @DisplayName("when delay < wheel duration")
    class WhenDelayLessThanWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(20, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(20L, 120L));
      }
    }

    @Nested
    @DisplayName("when delay = wheel duration")
    class WhenDelayEqualWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(50, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(50L, 150L));
      }
    }

    @Nested
    @DisplayName("when delay > wheel duration x 2")
    class WhenDelayGreaterThanDoubleWheelDuration {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(120, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(120L, 220L));
      }
    }

    @Nested
    @DisplayName("when delay is large")
    class WhenDelayIsLarge {

      @Test
      @DisplayName("then task executed")
      void thenTaskExecuted() {
        AtomicReference<Long> ref = new AtomicReference<>();
        long startAt = System.currentTimeMillis();
        timer.schedule(300, timeout -> ref.set(System.currentTimeMillis() - startAt));

        await()
            .pollInterval(Duration.ofMillis(50))
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isBetween(300L, 400L));
      }
    }
  }
}
