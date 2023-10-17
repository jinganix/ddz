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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.netty.util.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DelegatedTimeout")
class DelegatedTimeoutTest {

  Timeout timeout;

  DelegatedTimeout delegatedTimeout;

  @BeforeEach
  void setup() {
    timeout = mock(Timeout.class);
    delegatedTimeout = new DelegatedTimeout();
    delegatedTimeout.setDelegate(timeout);
  }

  @Nested
  @DisplayName("timer")
  class Timer {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        delegatedTimeout.timer();
        verify(timeout, times(1)).timer();
      }
    }
  }

  @Nested
  @DisplayName("task")
  class Task {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        delegatedTimeout.task();
        verify(timeout, times(1)).task();
      }
    }
  }

  @Nested
  @DisplayName("isExpired")
  class IsExpired {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        delegatedTimeout.isExpired();
        verify(timeout, times(1)).isExpired();
      }
    }
  }

  @Nested
  @DisplayName("isCancelled")
  class IsCancelled {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        delegatedTimeout.isCancelled();
        verify(timeout, times(1)).isCancelled();
      }
    }
  }

  @Nested
  @DisplayName("cancel")
  class Cancel {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then call delegate")
      void thenCallDelegate() {
        delegatedTimeout.cancel();
        verify(timeout, times(1)).cancel();
      }
    }
  }
}
