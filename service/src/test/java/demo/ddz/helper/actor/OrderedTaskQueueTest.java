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

package demo.ddz.helper.actor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import demo.ddz.tests.TestUtils;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ChainedTaskQueue")
class OrderedTaskQueueTest {

  ExecutorService executor() {
    return Executors.newVirtualThreadPerTaskExecutor();
  }

  @Nested
  @DisplayName("execute")
  class Execute {

    @Nested
    @DisplayName("when run two tasks")
    class WhenRunTwoTasks {

      @Test
      @DisplayName("then run tasks sequentially")
      void thenRunTasksSequentially() {
        OrderedTaskQueue taskQueue = new OrderedTaskQueue();
        ExecutorService executor = executor();
        taskQueue.execute(executor, () -> TestUtils.sleep(100));
        AtomicReference<Long> ref = new AtomicReference<>();
        long millis = System.currentTimeMillis();
        taskQueue.execute(executor, () -> ref.set(System.currentTimeMillis() - millis));

        await()
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isGreaterThanOrEqualTo(100));
      }
    }

    @Nested
    @DisplayName("when run two executors")
    class WhenRunTwoExecutors {

      @Test
      @DisplayName("then run tasks sequentially")
      void thenRunTasksSequentially() {
        OrderedTaskQueue taskQueue = new OrderedTaskQueue();
        taskQueue.execute(executor(), () -> TestUtils.sleep(100));
        AtomicReference<Long> ref = new AtomicReference<>();
        long millis = System.currentTimeMillis();
        taskQueue.execute(executor(), () -> ref.set(System.currentTimeMillis() - millis));

        await()
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isGreaterThanOrEqualTo(100));
      }
    }

    @Nested
    @DisplayName("when first task throw errors")
    class WhenFirstTaskThrowErrors {

      @Test
      @DisplayName("then run tasks sequentially")
      void thenRunTasksSequentially() {
        OrderedTaskQueue taskQueue = new OrderedTaskQueue();
        taskQueue.execute(
            executor(),
            () -> {
              TestUtils.sleep(100);
              throw new RuntimeException("error");
            });
        AtomicReference<Long> ref = new AtomicReference<>();
        long millis = System.currentTimeMillis();
        taskQueue.execute(executor(), () -> ref.set(System.currentTimeMillis() - millis));

        await()
            .atMost(Duration.ofSeconds(1))
            .untilAsserted(() -> assertThat(ref.get()).isGreaterThanOrEqualTo(100));
      }
    }
  }
}
