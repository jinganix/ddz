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

package io.github.jinganix.ddz.helper.actor;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.github.jinganix.ddz.tests.SpringIntegrationTests;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@DisplayName("VirtualThreadExecutor")
class VirtualThreadExecutorTest extends SpringIntegrationTests {

  @Autowired VirtualThreadExecutor executor;

  @Nested
  @DisplayName("publish")
  class Publish {

    @Nested
    @DisplayName("when called from thread")
    class WhenCalledFromThread {

      @Test
      @DisplayName("then run task in new virtual thread")
      void thenRunTaskInNewVirtualThread() {
        Thread thread = Thread.currentThread();
        StepVerifier.create(
                executor.publish(
                    () -> {
                      assertThat(Thread.currentThread().isVirtual()).isTrue();
                      assertThat(Thread.currentThread()).isNotEqualTo(thread);
                      return true;
                    }))
            .expectNext(true)
            .verifyComplete();
      }
    }

    @Nested
    @DisplayName("when called in virtual thread")
    class WhenCalledInVirtualThread {

      @Test
      @DisplayName("then run in current thread")
      void thenRunInCurrentThread() throws ExecutionException, InterruptedException {
        Executors.newVirtualThreadPerTaskExecutor()
            .submit(
                () -> {
                  Thread thread = Thread.currentThread();
                  StepVerifier.create(
                          executor.publish(
                              () -> {
                                assertThat(Thread.currentThread().isVirtual()).isTrue();
                                assertThat(Thread.currentThread()).isEqualTo(thread);
                                return true;
                              }))
                      .expectNext(true)
                      .verifyComplete();
                })
            .get();
      }
    }
  }
}
