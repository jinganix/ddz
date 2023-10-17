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

package io.github.jinganix.ddz.helper.phase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.netty.util.Timeout;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ScheduledPhase")
class ScheduledPhaseTest {

  @Nested
  @DisplayName("constructor")
  class Constructor {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then concrete")
      void thenConcrete() {
        PhasedContext phasedContext = mock(PhasedContext.class);
        assertThat(new ScheduledPhase(phasedContext).getContext()).isEqualTo(phasedContext);
      }
    }
  }

  @Nested
  @DisplayName("incrUid")
  class IncrUid {

    @Nested
    @DisplayName("when called")
    class WhenCalled {

      @Test
      @DisplayName("then increment uid")
      void thenIncrementUid() {
        ScheduledPhase phase = new ScheduledPhase(mock(PhasedContext.class));
        phase.incrUid();
        assertThat(phase.getUid()).isEqualTo(1);
      }
    }
  }

  @Nested
  @DisplayName("cancelTimer")
  class CancelTimer {

    @Nested
    @DisplayName("when timer is null")
    class WhenTimerIsNull {

      @Test
      @DisplayName("then do nothing")
      void thenDoNothing() {
        ScheduledPhase phase = new ScheduledPhase(mock(PhasedContext.class));
        phase.cancelTimer();
        assertThat(phase.getTimer()).isNull();
      }
    }

    @Nested
    @DisplayName("when timer is not null")
    class WhenTimerIsNotNull {

      @Test
      @DisplayName("then cancel")
      void thenCancel() {
        ScheduledPhase phase = new ScheduledPhase(mock(PhasedContext.class));
        Timeout timeout = mock(Timeout.class);
        phase.setTimer(timeout);
        phase.cancelTimer();

        assertThat(phase.getTimer()).isNull();
        verify(timeout, times(1)).cancel();
      }
    }
  }
}
