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

package io.github.jinganix.ddz.helper.actor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.jinganix.peashooter.DefaultTracer;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DisplayName("OrderedTaskExecutor")
class OrderedTaskExecutorTest {

  OrderedTraceExecutor executor =
      spy(
          new OrderedTraceExecutor(
              new CachedTaskQueueProvider(), new VirtualTraceExecutor(new DefaultTracer())));

  @Nested
  @DisplayName("publish")
  class Publish {

    @Nested
    @DisplayName("when run two publishers")
    class WhenRunTwoPublishers {

      @Nested
      @DisplayName("when run two same keys")
      class WhenRunTwoSameKeys {

        @Test
        @DisplayName("then run both sequentially")
        void thenRunBothSequentially() {
          long start = System.currentTimeMillis();
          StepVerifier.create(
                  Flux.zip(
                      executor.publish("a", () -> Mono.delay(Duration.ofMillis(200))),
                      executor.publish("a", () -> Mono.delay(Duration.ofMillis(200)))))
              .assertNext(
                  value -> {
                    long end = System.currentTimeMillis();
                    assertThat(end - start).isGreaterThanOrEqualTo(400);
                  })
              .verifyComplete();
        }
      }

      @Nested
      @DisplayName("when run two different keys")
      class WhenRunTwoDifferentKeys {

        @Test
        @DisplayName("then run both concurrently")
        void thenRunBothConcurrently() {
          long start = System.currentTimeMillis();
          StepVerifier.create(
                  Flux.zip(
                      executor.publish("a", () -> Mono.delay(Duration.ofMillis(500))),
                      executor.publish("b", () -> Mono.delay(Duration.ofMillis(500)))))
              .assertNext(
                  value -> {
                    long end = System.currentTimeMillis();
                    assertThat(end - start).isLessThan(1000);
                  })
              .verifyComplete();
        }
      }
    }

    @Nested
    @DisplayName("when task throw error")
    class WhenTaskThrowError {

      @Test
      @DisplayName("then publish error")
      void thenPublishError() {
        StepVerifier.create(executor.publish("a", () -> Mono.error(new RuntimeException("error"))))
            .expectError()
            .verify();
      }
    }
  }

  @Nested
  @DisplayName("supply(Long key, Supplier<R> supplier)")
  class SupplyObjectKey {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        Supplier<Integer> supplier = () -> 1;
        assertThat(executor.supply(1, supplier)).isEqualTo(1);
        verify(executor, times(1)).supply("1", supplier);
      }
    }
  }

  @Nested
  @DisplayName("executeAsync(Object key, Runnable task)")
  class ExecuteAsyncObjectKey {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        Runnable task = () -> {};
        executor.executeAsync(1, task);
        verify(executor, times(1)).executeAsync("1", task);
      }
    }
  }

  @Nested
  @DisplayName("executeSync(Object key, Runnable task)")
  class ExecuteSyncObjectKey {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        Runnable task = () -> {};
        executor.executeSync(1, task);
        verify(executor, times(1)).executeSync("1", task);
      }
    }
  }
}
